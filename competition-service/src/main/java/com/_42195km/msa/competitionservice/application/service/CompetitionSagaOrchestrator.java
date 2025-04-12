package com._42195km.msa.competitionservice.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com._42195km.msa.common.exception.CustomBusinessException;
import com._42195km.msa.competitionservice.application.dto.response.CompetitionAppResponseDto;
import com._42195km.msa.competitionservice.application.event.ApplicationSagaEvent;
import com._42195km.msa.competitionservice.application.event.CancellationSagaEvent;
import com._42195km.msa.competitionservice.application.event.PaymentSagaEvent;
import com._42195km.msa.competitionservice.application.event.SagaEvent;
import com._42195km.msa.competitionservice.application.exception.CompetitionServiceCode;
import com._42195km.msa.competitionservice.domain.model.Competition;
import com._42195km.msa.competitionservice.domain.model.ReceptionType;
import com._42195km.msa.competitionservice.domain.model.SagaState;
import com._42195km.msa.competitionservice.domain.model.SagaStep;
import com._42195km.msa.competitionservice.infrastructure.persistence.CompetitionParticipantMappingRepositoryImpl;
import com._42195km.msa.competitionservice.infrastructure.persistence.CompetitionRepositoryImpl;
import com._42195km.msa.competitionservice.infrastructure.persistence.SagaStateRepository;
import com._42195km.msa.competitionservice.presentation.dto.request.CancelParticipantRequestDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompetitionSagaOrchestrator {

	private static final String SAGA_TOPIC = "competition_saga";

	private final KafkaTemplate<String, SagaEvent> kafkaTemplate;
	private final SagaStateRepository sagaStateRepository;
	private final CompetitionService competitionService;
	private final ParticipantService participantService;
	private final CompetitionRepositoryImpl competitionRepository;
	private final CompetitionParticipantMappingRepositoryImpl mappingRepository;

	public String startApplicationSaga(UUID competitionId, UUID participantId) {
		SagaState sagaState = new SagaState("APPLICATION", competitionId, participantId);
		sagaState.setNextStep(SagaStep.TERMS_AGREEMENT);

		sagaStateRepository.saveSagaState(sagaState);

		log.info("Started new application saga with ID: {}", sagaState.getSagaId());
		return sagaState.getSagaId();
	}

	// 약관 동의 단계 처리
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
	public void initiatePayment(String sagaId, UUID competitionId, UUID participantId,
		String paymentMethod) {

		CompetitionAppResponseDto competition = competitionService.getCompetition(competitionId);
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
	public void completePayment(String sagaId, UUID competitionId, UUID participantId,
		Integer amount, String paymentMethod, String paymentStatus, String transactionId) {
		SagaState sagaState = sagaStateRepository.getSagaState(sagaId);
		if (sagaState == null) {
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_APPLY_FAIL);
		}

		try {
			// 결제 완료 이벤트 발행
			PaymentSagaEvent event = PaymentSagaEvent.createPaymentProcessedEvent(
				sagaId, competitionId, participantId, amount, paymentMethod, paymentStatus, transactionId, false);

			// 상태 업데이트
			sagaState.updatePaymentStatus(paymentStatus);
			sagaState.updatePaymentTransactionId(transactionId);
			sagaState.markStepAsCompleted(SagaStep.PAYMENT_PROCESSED);

			// 결제 성공 시 참가 확정 단계로 진행
			if ("SUCCESS".equals(paymentStatus)) {
				//sagaState.setNextStep(SagaStep.PARTICIPATION_CONFIRMED);
				//sagaStateRepository.saveSagaState(sagaState);
				sagaState.setNextStep(SagaStep.ELIGIBILITY_CHECK);
				sagaStateRepository.saveSagaState(sagaState);

				// 이벤트 발행
				sendEvent(event);
				log.info("Completed payment step for saga ID: {}", sagaId);

				// 참가 자격 검사 진행 (마감 여부, 중복 신청 여부 확인)
				checkEligibility(sagaId, competitionId, participantId);
			} else {
				// 결제 실패 처리
				log.error("Payment failed for saga ID: {}", sagaId);
				handleStepFailure(sagaState, SagaStep.PAYMENT_PROCESSED);
			}

		}catch (CustomBusinessException e) {
			log.error("결제 오류 추적 : {}", e.getMessage());
		}
		catch (Exception e) {
			log.error("Error completing payment: {}", e.getMessage(), e);
			handleStepFailure(sagaState, SagaStep.PAYMENT_PROCESSED);
		}
	}

	// 참가 자격 검사 단계 추가
	public void checkEligibility(String sagaId, UUID competitionId, UUID participantId) {
		SagaState sagaState = sagaStateRepository.getSagaState(sagaId);
		if (sagaState == null) {
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_APPLY_FAIL);
		}

		if (sagaState.getCurrentStep() != SagaStep.ELIGIBILITY_CHECK) {
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_APPLY_FAIL);
		}

		try {
			Competition competition = competitionRepository.findById(competitionId);

			// 중복 참가 신청 확인
			Boolean checkDupl = mappingRepository.checkIsParticipate(participantId, competitionId);
			if (checkDupl) {
				log.error("중복 신청 발견: {}", participantId);
				// duplicate 상태 업데이트
				sagaState.updateEligibilityStatus("DUPLICATE");
				sagaState.updateEligibilityReason("Duplicate application detected");
				sagaState.markStepAsCompleted(SagaStep.ELIGIBILITY_CHECK);
				sagaState.markAsFailed();
				sagaStateRepository.saveSagaState(sagaState);
				throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_APPLY_EXIST);
			}

			// 대회 신청 마감 확인 (선착순인 경우)
			if (competition.getReceptionType() == ReceptionType.FIRST) {
				long participantCount = mappingRepository.countByCompetition(competition);
				log.info("현재 참가자 수: {}, 최대 참가자 수: {}", participantCount, competition.getParticipantsNum());
				if (participantCount >= competition.getParticipantsNum()) {
					sagaState.updateEligibilityStatus("FULL");
					sagaState.updateEligibilityReason("Competition is full");
					sagaState.markStepAsCompleted(SagaStep.ELIGIBILITY_CHECK);
					sagaState.markAsFailed();
					sagaStateRepository.saveSagaState(sagaState);
					throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_APPLY_FIRST_FAIL);
				}
			}

			// 자격 검사 통과 시 상태 업데이트
			sagaState.markStepAsCompleted(SagaStep.ELIGIBILITY_CHECK);
			sagaState.setNextStep(SagaStep.PARTICIPATION_CONFIRMED);
			sagaStateRepository.saveSagaState(sagaState);

			log.info("Eligibility check passed for saga ID: {}", sagaId);

			// 참가 확정 진행
			confirmParticipation(sagaId, competitionId, participantId);

		} catch (CustomBusinessException e) {
			log.error("Eligibility check failed: {}", e.getMessage());
			sagaState.updateEligibilityStatus("FAILED");
			sagaState.updateEligibilityReason(e.getMessage());
			handleStepFailure(sagaState, SagaStep.ELIGIBILITY_CHECK);
			throw e;
		} catch (Exception e) {
			log.error("Error during eligibility check: {}", e.getMessage(), e);
			handleStepFailure(sagaState, SagaStep.ELIGIBILITY_CHECK);
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_APPLY_FAIL);
		}
	}

	// 참가 확정 단계 처리
	public void confirmParticipation(String sagaId, UUID competitionId, UUID participantId) {
		SagaState sagaState = sagaStateRepository.getSagaState(sagaId);
		if (sagaState == null) {
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_APPLY_FAIL);
		}

		if (sagaState.getCurrentStep() != SagaStep.PARTICIPATION_CONFIRMED) {
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_APPLY_FAIL);
		}

		try {
			// 대회 참가 확정 처리 - CompetitionService의 기존 메서드 활용
			competitionService.applyCompetition(competitionId, participantId);

			// 상태 업데이트
			sagaState.markStepAsCompleted(SagaStep.PARTICIPATION_CONFIRMED);
			sagaState.setNextStep(SagaStep.NOTIFICATION_SENT);
			sagaStateRepository.saveSagaState(sagaState);

			log.info("Confirmed participation for saga ID: {}", sagaId);

			// 알림 발송 처리
			sendNotification(sagaId, competitionId, participantId);

		} catch (Exception e) {
			log.error("Error confirming participation: {}", e.getMessage(), e);
			handleStepFailure(sagaState, SagaStep.PARTICIPATION_CONFIRMED);
		}
	}

	// 알림 발송 단계 처리
	public void sendNotification(String sagaId, UUID competitionId, UUID participantId) {
		SagaState sagaState = sagaStateRepository.getSagaState(sagaId);
		if (sagaState == null) {
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_APPLY_FAIL);
		}

		try {
			// TODO: 실제 알림 발송 로직 구현 (이메일, 푸시 등)
			log.info("Sending notification for saga ID: {}", sagaId);

			// 상태 업데이트
			sagaState.markStepAsCompleted(SagaStep.NOTIFICATION_SENT);
			sagaState.markAsCompleted();
			sagaStateRepository.saveSagaState(sagaState);

			log.info("Completed application saga with ID: {}", sagaId);

		} catch (Exception e) {
			log.error("Error sending notification: {}", e.getMessage(), e);
			// 알림 발송 실패는 치명적이지 않으므로, Saga를 실패로 처리하지 않고 완료 처리
			sagaState.markAsCompleted();
			sagaStateRepository.saveSagaState(sagaState);
			log.warn("Completed saga despite notification failure: {}", sagaId);
		}
	}

	// 취소 Saga 시작
	public String startCancellationSaga(UUID competitionId, UUID participantId, String reason, boolean refundRequired) {
		// 새 Saga 상태 생성
		SagaState sagaState = new SagaState("CANCELLATION", competitionId, participantId);
		sagaState.updateCancellationReason(reason);
		sagaState.updateRefundRequired(refundRequired);
		sagaState.setNextStep(SagaStep.CANCELLATION_REQUESTED);

		// 저장
		sagaStateRepository.saveSagaState(sagaState);

		// 취소 요청 이벤트 발행
		CancellationSagaEvent event = CancellationSagaEvent.createCancellationRequestedEvent(
			sagaState.getSagaId(), competitionId, participantId, reason, refundRequired, false);

		sendEvent(event);

		log.info("Started new cancellation saga with ID: {}", sagaState.getSagaId());

		// 취소 처리 시작
		processCancellation(sagaState.getSagaId(), competitionId, participantId);

		return sagaState.getSagaId();
	}

	// 취소 처리
	private void processCancellation(String sagaId, UUID competitionId, UUID participantId) {
		SagaState sagaState = sagaStateRepository.getSagaState(sagaId);
		if (sagaState == null) {
			throw CustomBusinessException.from(CompetitionServiceCode.PARTICIPANT_CANCEL_FAIL);
		}

		try {
			// 대회 취소 처리 - ParticipantService의 기존 메서드 활용
			participantService.cancelParticipant(new CancelParticipantRequestDto(competitionId, participantId, "일정변동",true));

			// 상태 업데이트
			sagaState.markStepAsCompleted(SagaStep.CANCELLATION_REQUESTED);

			// 환불이 필요한 경우
			if (Boolean.TRUE.equals(sagaState.getRefundRequired())) {
				sagaState.setNextStep(SagaStep.REFUND_INITIATED);
				sagaStateRepository.saveSagaState(sagaState);

				// 환불 처리 시작
				initiateRefund(sagaId, competitionId, participantId);
			} else {
				// 환불이 필요 없는 경우 Saga 완료
				sagaState.markAsCompleted();
				sagaStateRepository.saveSagaState(sagaState);
				log.info("Completed cancellation saga with ID: {}", sagaId);
			}

		} catch (Exception e) {
			log.error("Error processing cancellation: {}", e.getMessage(), e);
			sagaState.markAsFailed();
			sagaStateRepository.saveSagaState(sagaState);
			throw CustomBusinessException.from(CompetitionServiceCode.PARTICIPANT_CANCEL_FAIL);
		}
	}

	// 환불 시작 처리
	private void initiateRefund(String sagaId, UUID competitionId, UUID participantId) {
		SagaState sagaState = sagaStateRepository.getSagaState(sagaId);
		if (sagaState == null) {
			throw CustomBusinessException.from(CompetitionServiceCode.PARTICIPANT_CANCEL_FAIL);
		}

		try {
			// 환불 시작 이벤트 발행
			CancellationSagaEvent event = CancellationSagaEvent.createRefundInitiatedEvent(
				sagaId, competitionId, participantId, sagaState.getCancellationReason(), true, false);

			// 상태 업데이트
			sagaState.updateRefundStatus("INITIATED");
			sagaState.markStepAsCompleted(SagaStep.REFUND_INITIATED);
			sagaState.setNextStep(SagaStep.REFUND_PROCESSED);
			sagaStateRepository.saveSagaState(sagaState);

			sendEvent(event);

			log.info("Initiated refund for saga ID: {}", sagaId);
			// 가상으로 환불 완료 처리
			completeRefund(sagaId, competitionId, participantId, "SUCCESS");

		} catch (Exception e) {
			log.error("Error initiating refund: {}", e.getMessage(), e);
			sagaState.markAsFailed();
			sagaStateRepository.saveSagaState(sagaState);
			throw CustomBusinessException.from(CompetitionServiceCode.PARTICIPANT_CANCEL_FAIL);
		}
	}

	// 환불 완료 처리
	private void completeRefund(String sagaId, UUID competitionId, UUID participantId, String refundStatus) {
		SagaState sagaState = sagaStateRepository.getSagaState(sagaId);
		if (sagaState == null) {
			throw CustomBusinessException.from(CompetitionServiceCode.PARTICIPANT_CANCEL_FAIL);
		}

		try {
			// 환불 완료 이벤트 발행
			CancellationSagaEvent event = CancellationSagaEvent.createRefundProcessedEvent(
				sagaId, competitionId, participantId, sagaState.getCancellationReason(), true, refundStatus, false);

			// 상태 업데이트
			sagaState.updateRefundStatus(refundStatus);
			sagaState.markStepAsCompleted(SagaStep.REFUND_PROCESSED);
			sagaState.markAsCompleted();
			sagaStateRepository.saveSagaState(sagaState);

			sendEvent(event);

			log.info("Completed refund process for saga ID: {}", sagaId);

		} catch (Exception e) {
			log.error("Error completing refund: {}", e.getMessage(), e);
			sagaState.markAsFailed();
			sagaStateRepository.saveSagaState(sagaState);
			throw CustomBusinessException.from(CompetitionServiceCode.PARTICIPANT_CANCEL_FAIL);
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
				participantService.cancelParticipant(new CancelParticipantRequestDto(sagaState.getCompetitionId(), sagaState.getParticipantId(), "일정변동",true));
				log.info("Compensated participation confirmation for saga: {}", sagaState.getSagaId());
			} catch (Exception e) {
				log.error("Failed to compensate participation confirmation: {}", e.getMessage());
				throw e;
			}
		}

		if (completedSteps.contains(SagaStep.PAYMENT_PROCESSED)) {
			// 결제 완료 보상: 환불 처리
			try {
				// TODO: 실제 환불 처리 로직 구현
				log.info("Compensated payment for saga: {}", sagaState.getSagaId());
			} catch (Exception e) {
				log.error("Failed to compensate payment: {}", e.getMessage());
				throw e;
			}
		}

		// 다른 단계에 대한 보상 처리는 필요 없음 (읽기 전용 작업)
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
