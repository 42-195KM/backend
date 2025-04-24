package com._42195km.msa.competitionservice.infrastructure.messaging;

import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com._42195km.msa.competitionservice.application.event.ApplicationSagaEvent;
import com._42195km.msa.competitionservice.application.event.CancellationSagaEvent;
import com._42195km.msa.competitionservice.application.event.PaymentSagaEvent;
import com._42195km.msa.competitionservice.application.event.SagaEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventPublisher implements EventPublisher{
	private final KafkaTemplate<String, SagaEvent> sagaEventKafkaTemplate;
	private final KafkaTemplate<String, Object> notificationKafkaTemplate;

	@Override
	public <T extends SagaEvent> void publishSagaEvent(String topic, String key, T event) {
		try {
			sagaEventKafkaTemplate.send(topic, key, event);
			log.info("Sent saga event: {} for saga: {}", event.getStep(), event.getSagaId());
		} catch (Exception e) {
			log.error("Failed to send saga event: {}", e.getMessage(), e);
			throw new RuntimeException("Failed to send saga event", e);
		}
	}

	@Override
	public void publishCompensationEvent(String topic, String sagaId, SagaEvent originalEvent) {
		try {
			SagaEvent compensationEvent = createCompensationEvent(originalEvent);
			if (compensationEvent != null) {
				sagaEventKafkaTemplate.send(topic, sagaId, compensationEvent);
				log.info("Sent compensation event for saga: {}, step: {}",
					sagaId, compensationEvent.getStep());
			} else {
				log.warn("Could not create compensation event for saga: {}", sagaId);
			}
		} catch (Exception e) {
			log.error("Failed to send compensation event: {}", e.getMessage(), e);
			throw new RuntimeException("Failed to send compensation event", e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")  // Type safety for notificationKafkaTemplate
	public <T> void publishNotificationEvent(String topic, String key, T notification) {
		try {
			notificationKafkaTemplate.send(topic, key, notification);
			log.info("Sent notification event to topic: {}, key: {}", topic, key);
		} catch (Exception e) {
			log.error("Failed to send notification event: {}", e.getMessage(), e);
			throw new RuntimeException("Failed to send notification event", e);
		}
	}

	/**
	 * 원본 이벤트로부터 보상 이벤트를 생성합니다.
	 *
	 * @param originalEvent 원본 이벤트
	 * @return 보상 이벤트
	 */
	private SagaEvent createCompensationEvent(SagaEvent originalEvent) {
		if (originalEvent instanceof PaymentSagaEvent paymentEvent) {
			return PaymentSagaEvent.createPaymentProcessedEvent(
				paymentEvent.getSagaId(),
				paymentEvent.getCompetitionId(),
				paymentEvent.getParticipantId(),
				paymentEvent.getAmount(),
				paymentEvent.getPaymentMethod(),
				"COMPENSATION",
				paymentEvent.getPaymentTransactionId(),
				true  // compensation 플래그 설정
			);
		} else if (originalEvent instanceof ApplicationSagaEvent applicationEvent) {
			// ApplicationSagaEvent 타입에 따른 보상 이벤트 생성
			return switch (originalEvent.getStep()) {
				case TERMS_AGREEMENT -> ApplicationSagaEvent.createTermsEvent(
					applicationEvent.getSagaId(),
					applicationEvent.getCompetitionId(),
					applicationEvent.getParticipantId(),
					false,  // terms not agreed for compensation
					true    // compensation 플래그
				);
				case SOUVENIR_SELECTION -> ApplicationSagaEvent.createSouvenirEvent(
					applicationEvent.getSagaId(),
					applicationEvent.getCompetitionId(),
					applicationEvent.getParticipantId(),
					null,   // souvenir selection null for compensation
					true    // compensation 플래그
				);
				case SHIPPING_ADDRESS -> ApplicationSagaEvent.createShippingEvent(
					applicationEvent.getSagaId(),
					applicationEvent.getCompetitionId(),
					applicationEvent.getParticipantId(),
					null,   // shipping address null for compensation
					true    // compensation 플래그
				);
				default -> null;
			};
		} else if (originalEvent instanceof CancellationSagaEvent cancellationEvent) {
			// CancellationSagaEvent 보상 이벤트 생성
			return switch (originalEvent.getStep()) {
				case CANCELLATION_REQUESTED -> CancellationSagaEvent.createCancellationRequestedEvent(
					cancellationEvent.getSagaId(),
					cancellationEvent.getCompetitionId(),
					cancellationEvent.getParticipantId(),
					"Compensation: " + cancellationEvent.getCancellationReason(),
					false,  // refund not required for compensation
					true    // compensation 플래그
				);
				default -> null;
			};
		}

		log.warn("Unknown event type for compensation: {}", originalEvent.getClass().getName());
		return null;
	}

	/**
	 * 경쟁 이벤트를 특정 사용자에게 전송합니다.
	 *
	 * @param userId 사용자 ID
	 * @param competitionTitle 대회 제목
	 */
	public void sendCompetitionNotification(UUID userId, String competitionTitle) {
		CompetitionApplyNotificationDto notification = CompetitionApplyNotificationDto.builder()
			.userId(userId)
			.mediaId("U087R317SMN")  // 미디어 ID 설정
			.title(competitionTitle)
			.build();

		this.publishNotificationEvent("competition_notification", userId.toString(), notification);
	}
}
