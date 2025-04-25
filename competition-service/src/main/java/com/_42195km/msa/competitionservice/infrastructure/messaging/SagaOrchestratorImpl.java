package com._42195km.msa.competitionservice.infrastructure.messaging;

import java.util.List;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com._42195km.msa.common.exception.CustomBusinessException;
import com._42195km.msa.competitionservice.application.dto.response.CompetitionAppResponseDto;
import com._42195km.msa.competitionservice.application.event.ApplicationSagaEvent;
import com._42195km.msa.competitionservice.application.event.PaymentSagaEvent;
import com._42195km.msa.competitionservice.application.event.SagaEvent;
import com._42195km.msa.competitionservice.application.exception.CompetitionServiceCode;
import com._42195km.msa.competitionservice.application.service.CompetitionServiceImpl;
import com._42195km.msa.competitionservice.application.service.ParticipantServiceImpl;
import com._42195km.msa.competitionservice.domain.model.SagaState;
import com._42195km.msa.competitionservice.domain.model.SagaStep;
import com._42195km.msa.competitionservice.infrastructure.persistence.SagaStateRepository;
import com._42195km.msa.competitionservice.presentation.dto.request.CancelParticipantRequestDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SagaOrchestratorImpl implements SagaOrchestrator{

	private static final String SAGA_TOPIC = "competition_saga";

	private final KafkaTemplate<String, SagaEvent> kafkaTemplate;
	private final SagaStateRepository sagaStateRepository;
	private final CompetitionServiceImpl competitionServiceImpl;
	private final ParticipantServiceImpl participantService;

	@Override
	public String startApplicationSaga(UUID competitionId, UUID participantId) {
		SagaState sagaState = new SagaState("APPLICATION", competitionId, participantId);
		sagaState.setNextStep(SagaStep.TERMS_AGREEMENT);

		sagaStateRepository.saveSagaState(sagaState);

		log.info("Started new application saga with ID: {}", sagaState.getSagaId());
		return sagaState.getSagaId();
	}

	// 약관 동의 단계 처리
	@Override
	public void processTermsAgreement(String sagaId, UUID competitionId, UUID participantId, Boolean termsAgreed) {
		SagaState sagaState = sagaStateRepository.getSagaState(sagaId);
		if (sagaState == null) {
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_APPLY_FAIL);
		}

		if (sagaState.getCurrentStep() != SagaStep.TERMS_AGREEMENT) {
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_APPLY_FAIL);
		}

		try {
			// 약관 동의 이벤트 발행
			ApplicationSagaEvent event = ApplicationSagaEvent.createTermsEvent(
				sagaId, competitionId, participantId, termsAgreed, false);

			// 상태 업데이트
			sagaState.updateTermsAgreed(termsAgreed);
			sagaState.markStepAsCompleted(SagaStep.TERMS_AGREEMENT);
			sagaState.setNextStep(SagaStep.SOUVENIR_SELECTION);
			sagaStateRepository.saveSagaState(sagaState);

			// 이벤트 발행
			sendEvent(event);

			log.info("Processed terms agreement step for saga ID: {}", sagaId);
		} catch (Exception e) {
			log.error("Error processing terms agreement: {}", e.getMessage(), e);
			sagaState.markAsFailed();
			sagaStateRepository.saveSagaState(sagaState);
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_APPLY_FAIL);
		}
	}

	// 기념품 선택 단계 처리
	@Override
	public void processSouvenirSelection(String sagaId, UUID competitionId, UUID participantId,
		String souvenirSelection) {
		SagaState sagaState = sagaStateRepository.getSagaState(sagaId);
		if (sagaState == null) {
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_APPLY_FAIL);
		}

		if (sagaState.getCurrentStep() != SagaStep.SOUVENIR_SELECTION) {
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_APPLY_FAIL);
		}

		try {
			// 기념품 선택 이벤트 발행
			ApplicationSagaEvent event = ApplicationSagaEvent.createSouvenirEvent(
				sagaId, competitionId, participantId, souvenirSelection, false);

			// 상태 업데이트
			sagaState.updateSouvenirSelection(souvenirSelection);
			sagaState.markStepAsCompleted(SagaStep.SOUVENIR_SELECTION);
			sagaState.setNextStep(SagaStep.SHIPPING_ADDRESS);
			sagaStateRepository.saveSagaState(sagaState);

			// 이벤트 발행
			sendEvent(event);

			log.info("Processed souvenir selection step for saga ID: {}", sagaId);
		} catch (Exception e) {
			log.error("Error processing souvenir selection: {}", e.getMessage(), e);
			handleStepFailure(sagaState, SagaStep.SOUVENIR_SELECTION);
		}
	}

	// 배송지 입력 단계 처리
	@Override
	public void processShippingAddress(String sagaId, UUID competitionId, UUID participantId, String shippingAddress) {
		SagaState sagaState = sagaStateRepository.getSagaState(sagaId);
		if (sagaState == null) {
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_APPLY_FAIL);
		}

		if (sagaState.getCurrentStep() != SagaStep.SHIPPING_ADDRESS) {
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_APPLY_FAIL);
		}

		try {
			// 배송지 입력 이벤트 발행
			ApplicationSagaEvent event = ApplicationSagaEvent.createShippingEvent(
				sagaId, competitionId, participantId, shippingAddress, false);

			// 상태 업데이트
			sagaState.updateShippingAddress(shippingAddress);
			sagaState.markStepAsCompleted(SagaStep.SHIPPING_ADDRESS);
			sagaState.setNextStep(SagaStep.PAYMENT_INITIATED);
			sagaStateRepository.saveSagaState(sagaState);

			// 이벤트 발행
			sendEvent(event);

			log.info("Processed shipping address step for saga ID: {}", sagaId);

		} catch (Exception e) {
			log.error("Error processing shipping address: {}", e.getMessage(), e);
			handleStepFailure(sagaState, SagaStep.SHIPPING_ADDRESS);
		}
	}

	// 결제 시작 단계 처리
	@Override
	public void initiatePayment(String sagaId, UUID competitionId, UUID participantId,
		String paymentMethod) {

		CompetitionAppResponseDto competition = competitionServiceImpl.getCompetition(competitionId);
		Integer amount = competition.getPrice();

		SagaState sagaState = sagaStateRepository.getSagaState(sagaId);

		if (sagaState == null) {
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_APPLY_FAIL);
		}

		try {
			// 결제 시작 이벤트 발행
			PaymentSagaEvent event = PaymentSagaEvent.createPaymentInitiatedEvent(
				sagaId, competitionId, participantId, amount, paymentMethod, false);

			// 상태 업데이트
			sagaState.updateAmount(amount);
			sagaState.updatePaymentMethod(paymentMethod);
			sagaState.updatePaymentStatus("INITIATED");
			sagaState.markStepAsCompleted(SagaStep.PAYMENT_INITIATED);
			sagaState.setNextStep(SagaStep.PAYMENT_PROCESSED);
			sagaStateRepository.saveSagaState(sagaState);

			// 이벤트 발행
			sendEvent(event);

			log.info("Initiated payment step for saga ID: {}", sagaId);

		} catch (Exception e) {
			log.error("Error initiating payment: {}", e.getMessage(), e);
			handleStepFailure(sagaState, SagaStep.PAYMENT_INITIATED);
		}
	}

	// 결제 완료 단계 처리
	@Override
	public void completePayment(String sagaId, UUID competitionId, UUID participantId,
		Integer amount, String paymentMethod, String paymentStatus, String transactionId) {
		SagaState sagaState = sagaStateRepository.getSagaState(sagaId);
		if (sagaState == null) {
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_APPLY_FAIL);
		}

		try {
			// 상태 업데이트
			sagaState.updatePaymentStatus(paymentStatus);
			sagaState.updatePaymentTransactionId(transactionId);
			sagaState.markStepAsCompleted(SagaStep.PAYMENT_PROCESSED);

			// 다음 단계는 이벤트 소비자에서 처리하도록 변경
			sagaState.setNextStep(SagaStep.ELIGIBILITY_CHECK);
			sagaStateRepository.saveSagaState(sagaState);

			// 결제 완료 이벤트 발행
			PaymentSagaEvent event = PaymentSagaEvent.createPaymentProcessedEvent(
				sagaId, competitionId, participantId, amount, paymentMethod, paymentStatus, transactionId, false);

			// 이벤트 발행
			sendEvent(event);
			log.info("Completed payment step for saga ID: {}", sagaId);

		} catch (Exception e) {
			log.error("Error completing payment: {}", e.getMessage(), e);
			handleStepFailure(sagaState, SagaStep.PAYMENT_PROCESSED);
		}
	}

	// 단계 실패 처리 및 보상 트랜잭션 실행
	private void handleStepFailure(SagaState sagaState, SagaStep failedStep) {
		log.info("Handling failure for step: {} in saga: {}", failedStep, sagaState.getSagaId());

		sagaState.startCompensation();
		sagaStateRepository.saveSagaState(sagaState);

		// 보상 트랜잭션 실행
		try {
			executeCompensation(sagaState, failedStep);
			sagaState.completeCompensation();
		} catch (Exception e) {
			log.error("Compensation failed for saga: {}", sagaState.getSagaId(), e);
			sagaState.markAsFailed();
		} finally {
			sagaStateRepository.saveSagaState(sagaState);
		}
	}

	// 보상 트랜잭션 실행
	private void executeCompensation(SagaState sagaState, SagaStep failedStep) {
		List<SagaStep> completedSteps = sagaState.getCompletedSteps();

		// 역순으로 보상 트랜잭션 실행
		if (completedSteps.contains(SagaStep.PARTICIPATION_CONFIRMED)) {
			// 참가 확정 보상: 참가 취소
			try {
				participantService.cancelParticipant(
					new CancelParticipantRequestDto(sagaState.getCompetitionId(), sagaState.getParticipantId(), "일정변동",
						true));
				log.info("Compensated participation confirmation for saga: {}", sagaState.getSagaId());
			} catch (Exception e) {
				log.error("Failed to compensate participation confirmation: {}", e.getMessage());
				throw e;
			}
		}

		if (completedSteps.contains(SagaStep.PAYMENT_PROCESSED)) {
			// 결제 완료 보상: 환불 처리
			try {
				// 현재는 로그만 남김. 환불 처리 미구현
				log.info("Compensated payment for saga: {}", sagaState.getSagaId());
			} catch (Exception e) {
				log.error("Failed to compensate payment: {}", e.getMessage());
				throw e;
			}
		}
	}

	// 이벤트 발행
	private void sendEvent(SagaEvent event) {
		String key = event.getSagaId();
		try {
			kafkaTemplate.send(SAGA_TOPIC, key, event);
			log.info("Sent saga event: {} for saga: {}", event.getStep(), event.getSagaId());
		} catch (Exception e) {
			log.error("Failed to send saga event: {}", e.getMessage(), e);
			throw new RuntimeException("Failed to send saga event", e);
		}
	}
}