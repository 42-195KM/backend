package com._42195km.msa.competitionservice.infrastructure.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com._42195km.msa.competitionservice.application.event.ApplicationSagaEvent;
import com._42195km.msa.competitionservice.application.event.CancellationSagaEvent;
import com._42195km.msa.competitionservice.application.event.PaymentSagaEvent;
import com._42195km.msa.competitionservice.application.event.SagaEvent;
import com._42195km.msa.competitionservice.application.service.CompetitionService;
import com._42195km.msa.competitionservice.domain.model.ParticipantDetail;
import com._42195km.msa.competitionservice.domain.model.SagaState;
import com._42195km.msa.competitionservice.domain.model.SagaStep;
import com._42195km.msa.competitionservice.domain.repository.ParticipantDetailRepository;
import com._42195km.msa.competitionservice.infrastructure.persistence.ParticipantDetailRepositoryImpl;
import com._42195km.msa.competitionservice.infrastructure.persistence.SagaStateRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SagaEventConsumer {

	private final SagaStateRepository sagaStateRepository;
	private final CompetitionService competitionService;
	private final ParticipantDetailRepositoryImpl participantDetailRepository;
	private final KafkaTemplate<String, SagaEvent> kafkaTemplate;

	@KafkaListener(
		topics = "competition_saga",
		groupId = "${spring.kafka.consumer.group-id}_saga",
		containerFactory = "sagaKafkaListenerContainerFactory"
	)
	public void consumeSagaEvent(SagaEvent event) {
		log.info("Received saga event: {} for saga: {}", event.getStep(), event.getSagaId());

		// 이벤트 타입에 따라 적절한 처리
		if (event instanceof ApplicationSagaEvent) {
			processApplicationEvent((ApplicationSagaEvent)event);
		} else if (event instanceof PaymentSagaEvent) {
			processPaymentEvent((PaymentSagaEvent)event);
		} else if (event instanceof CancellationSagaEvent) {
			processCancellationEvent((CancellationSagaEvent)event);
		} else {
			log.warn("Unknown event type received: {}", event.getClass().getName());
		}
	}

	private void processApplicationEvent(ApplicationSagaEvent event) {
		log.info("Processing application event: {} for saga: {}", event.getStep(), event.getSagaId());
		if (event.isCompensation()) {
			// 보상 트랜잭션 처리
			handleCompensationEvent(event);
			return;
		}

		SagaState sagaState = sagaStateRepository.getSagaState(event.getSagaId());
		if (sagaState == null) {
			log.error("Saga state not found for application event: {}", event.getSagaId());
			return;
		}

		// 이벤트 단계에 따른 처리
		switch (event.getStep()) {
			case TERMS_AGREEMENT:
				// 약관 동의 이벤트 처리
				log.info("Terms agreement processed for saga: {}", event.getSagaId());
				saveOrUpdateParticipantDetail(sagaState);
				break;
			case SOUVENIR_SELECTION:
				// 기념품 선택 이벤트 처리
				log.info("Souvenir selection processed for saga: {}", event.getSagaId());
				saveOrUpdateParticipantDetail(sagaState);
				break;
			case SHIPPING_ADDRESS:
				// 배송지 입력 이벤트 처리
				log.info("Shipping address processed for saga: {}", event.getSagaId());
				saveOrUpdateParticipantDetail(sagaState);
				break;
			default:
				log.warn("Unhandled application event step: {}", event.getStep());
		}
	}

	private void processPaymentEvent(PaymentSagaEvent event) {
		log.info("Processing payment event: {} for saga: {}", event.getStep(), event.getSagaId());
		if (event.isCompensation()) {
			// 보상 트랜잭션 처리
			handleCompensationEvent(event);
			return;
		}

		// 이벤트 단계에 따른 처리
		switch (event.getStep()) {
			case PAYMENT_INITIATED:
				// 결제 시작 이벤트 처리
				log.info("Payment initiated for saga: {}", event.getSagaId());
				break;

			case PAYMENT_PROCESSED:
				// 결제 완료 이벤트를 받아 대회 신청 완료 처리
				completeCompetitionApplication(event);
				break;

			default:
				log.warn("Unhandled payment event step: {}", event.getStep());
		}
	}

	/**
	 * 결제 완료 이벤트를 수신하여 대회 신청을 완료하는 메서드
	 */
	private void completeCompetitionApplication(PaymentSagaEvent event) {
		log.info("Completing competition application for saga: {}", event.getSagaId());
		try {
			// 1. Saga 상태 조회
			SagaState sagaState = sagaStateRepository.getSagaState(event.getSagaId());
			if (sagaState == null) {
				log.error("Saga state not found for ID: {}", event.getSagaId());
				return;
			}

			// 2. 결제 상태 확인
			if (!"SUCCESS".equals(event.getPaymentStatus())) {
				log.error("Payment not successful for saga: {}", event.getSagaId());
				return;
			}

			// 3. 참가 자격 검사 실행
			try {
				// 참가 자격 검사 로직 수행
				checkEligibilityAndConfirm(sagaState);
			} catch (Exception e) {
				log.error("Error during eligibility check: {}", e.getMessage());
				// 실패 시 보상 트랜잭션 시작 이벤트 발행
				sendCompensationEvent(event);
			}
		} catch (Exception e) {
			log.error("Error completing competition application: {}", e.getMessage(), e);
		}
	}

	/**
	 * 참가 자격 검사 및 신청 확정 처리
	 */
	@Transactional
	protected void checkEligibilityAndConfirm(SagaState sagaState) {
		try {
			// 1. 참가 자격 검사 상태로 업데이트
			sagaState.setNextStep(SagaStep.ELIGIBILITY_CHECK);
			sagaStateRepository.saveSagaState(sagaState);

			// 2. 중복 신청 확인
			boolean isDuplicate = competitionService.checkDuplicateApplication(
				sagaState.getCompetitionId(), sagaState.getParticipantId());

			if (isDuplicate) {
				sagaState.updateEligibilityStatus("DUPLICATE");
				sagaState.updateEligibilityReason("Duplicate application detected");
				sagaState.markAsFailed();
				sagaStateRepository.saveSagaState(sagaState);
				throw new RuntimeException("Duplicate application detected");
			}

			// 3. 대회 정원 확인
			boolean isFull = competitionService.checkCompetitionCapacity(sagaState.getCompetitionId());
			if (isFull) {
				sagaState.updateEligibilityStatus("FULL");
				sagaState.updateEligibilityReason("Competition is full");
				sagaState.markAsFailed();
				sagaStateRepository.saveSagaState(sagaState);
				throw new RuntimeException("Competition is full");
			}

			// 4. 자격 검사 통과 - 참가 확정 처리
			sagaState.markStepAsCompleted(SagaStep.ELIGIBILITY_CHECK);
			sagaState.setNextStep(SagaStep.PARTICIPATION_CONFIRMED);
			sagaStateRepository.saveSagaState(sagaState);

			// 5. 참가 확정 처리
			competitionService.applyCompetition(sagaState.getCompetitionId(), sagaState.getParticipantId());

			// 6. 참가 확정 완료 상태 업데이트
			sagaState.markStepAsCompleted(SagaStep.PARTICIPATION_CONFIRMED);
			sagaState.setNextStep(SagaStep.NOTIFICATION_SENT);
			sagaStateRepository.saveSagaState(sagaState);

			// 7. 알림 발송
			sendNotification(sagaState);

			// 8. Saga 완료
			sagaState.markAsCompleted();
			sagaStateRepository.saveSagaState(sagaState);

			log.info("Competition application completed successfully for saga: {}", sagaState.getSagaId());
		} catch (Exception e) {
			log.error("Error in eligibility check and confirmation: {}", e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * 알림 발송 처리
	 */
	private void sendNotification(SagaState sagaState) {
		try {
			// TODO : 알림 발송 로직 구현
			log.info("Sending notification for saga: {}", sagaState.getSagaId());

			// 알림 발송 완료 상태 업데이트
			sagaState.markStepAsCompleted(SagaStep.NOTIFICATION_SENT);
			sagaStateRepository.saveSagaState(sagaState);
		} catch (Exception e) {
			log.warn("Failed to send notification, but continuing: {}", e.getMessage());
		}
	}

	/**
	 * 보상 트랜잭션 이벤트 발행
	 */
	private void sendCompensationEvent(SagaEvent originalEvent) {
		try {
			// 원본 이벤트를 기반으로 보상 이벤트 생성
			SagaEvent compensationEvent = createCompensationEvent(originalEvent);

			// 보상 이벤트 발행
			kafkaTemplate.send("competition_saga", compensationEvent.getSagaId(), compensationEvent);
			log.info("Sent compensation event for saga: {}", originalEvent.getSagaId());
		} catch (Exception e) {
			log.error("Failed to send compensation event: {}", e.getMessage(), e);
		}
	}

	/**
	 * 보상 트랜잭션 이벤트 생성
	 */
	private SagaEvent createCompensationEvent(SagaEvent originalEvent) {
		if (originalEvent instanceof PaymentSagaEvent) {
			PaymentSagaEvent paymentEvent = (PaymentSagaEvent)originalEvent;
			return PaymentSagaEvent.createPaymentProcessedEvent(
				paymentEvent.getSagaId(),
				paymentEvent.getCompetitionId(),
				paymentEvent.getParticipantId(),
				paymentEvent.getAmount(),
				paymentEvent.getPaymentMethod(),
				"COMPENSATION",
				paymentEvent.getPaymentTransactionId(),
				true
			);
		}

		return null;
	}

	/**
	 * 보상 트랜잭션 이벤트 처리
	 */
	private void handleCompensationEvent(SagaEvent event) {
		log.info("Processing compensation event for saga: {}", event.getSagaId());

		try {
			// Saga 상태 조회
			SagaState sagaState = sagaStateRepository.getSagaState(event.getSagaId());
			if (sagaState == null) {
				log.error("Saga state not found for compensation: {}", event.getSagaId());
				return;
			}

			// 보상 트랜잭션 시작 상태로 업데이트
			sagaState.startCompensation();
			sagaStateRepository.saveSagaState(sagaState);

			// 보상 처리 로직 - 이벤트 타입 및 완료된 단계에 따라 다름
			if (event instanceof PaymentSagaEvent) {
				// 결제 관련 보상 처리
				handlePaymentCompensation((PaymentSagaEvent)event, sagaState);
			} else if (event instanceof ApplicationSagaEvent) {
				// 신청 관련 보상 처리
				handleApplicationCompensation((ApplicationSagaEvent)event, sagaState);
			}

			// 보상 완료 상태 업데이트
			sagaState.completeCompensation();
			sagaStateRepository.saveSagaState(sagaState);

			log.info("Compensation completed for saga: {}", event.getSagaId());
		} catch (Exception e) {
			log.error("Error during compensation: {}", e.getMessage(), e);
		}
	}

	private void handlePaymentCompensation(PaymentSagaEvent event, SagaState sagaState) {
		// 결제 단계 보상 처리 (환불 등)
		log.info("Processing payment compensation for saga: {}", event.getSagaId());
	}

	private void handleApplicationCompensation(ApplicationSagaEvent event, SagaState sagaState) {
		// 신청 단계 보상 처리
		log.info("Processing application compensation for saga: {}", event.getSagaId());
	}

	private void processCancellationEvent(CancellationSagaEvent event) {
		log.info("Processing cancellation event: {} for saga: {}", event.getStep(), event.getSagaId());

		// 취소 이벤트 처리 로직 구현
		switch (event.getStep()) {
			case CANCELLATION_REQUESTED:
				// 취소 요청 처리
				log.info("Cancellation requested for saga: {}", event.getSagaId());
				break;

			case REFUND_INITIATED:
				// 환불 시작 처리
				log.info("Refund initiated for saga: {}", event.getSagaId());
				break;

			case REFUND_PROCESSED:
				// 환불 완료 처리
				log.info("Refund processed for saga: {}", event.getSagaId());
				break;

			default:
				log.warn("Unhandled cancellation event step: {}", event.getStep());
		}
	}

	private void saveOrUpdateParticipantDetail(SagaState state) {
		try {
			ParticipantDetail detail = participantDetailRepository
				.findByCompetitionIdAndParticipantId(state.getCompetitionId(), state.getParticipantId())
				.orElse(ParticipantDetail.builder()
					.competitionId(state.getCompetitionId())
					.participantId(state.getParticipantId())
					.build());

			detail.update(state);


			participantDetailRepository.save(detail);
			log.info("ParticipantDetail saved for saga: {}", state.getSagaId());
		} catch (Exception e) {
			log.error("Failed to persist ParticipantDetail for saga {}: {}", state.getSagaId(), e.getMessage(), e);
		}
	}
}
