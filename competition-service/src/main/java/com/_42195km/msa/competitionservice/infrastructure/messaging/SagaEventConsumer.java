package com._42195km.msa.competitionservice.infrastructure.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com._42195km.msa.competitionservice.application.event.ApplicationSagaEvent;
import com._42195km.msa.competitionservice.application.event.CancellationSagaEvent;
import com._42195km.msa.competitionservice.application.event.PaymentSagaEvent;
import com._42195km.msa.competitionservice.application.event.SagaEvent;
import com._42195km.msa.competitionservice.application.service.CompetitionSagaOrchestrator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SagaEventConsumer {

	private final CompetitionSagaOrchestrator sagaOrchestrator;

	@KafkaListener(
		topics = "competition_saga",
		groupId = "${spring.kafka.consumer.group-id}_saga",
		containerFactory = "sagaKafkaListenerContainerFactory"
	)
	public void consumeSagaEvent(SagaEvent event) {
		log.info("Received saga event: {} for saga: {}", event.getStep(), event.getSagaId());

		// 이벤트 타입에 따라 적절한 처리
		if (event instanceof ApplicationSagaEvent) {
			processApplicationEvent((ApplicationSagaEvent) event);
		} else if (event instanceof PaymentSagaEvent) {
			processPaymentEvent((PaymentSagaEvent) event);
		} else if (event instanceof CancellationSagaEvent) {
			processCancellationEvent((CancellationSagaEvent) event);
		} else {
			log.warn("Unknown event type received: {}", event.getClass().getName());
		}
	}

	private void processApplicationEvent(ApplicationSagaEvent event) {
		log.info("Processing application event: {} for saga: {}", event.getStep(), event.getSagaId());
		// 이벤트를 오케스트레이터로 전달
		// 여기서는 이벤트가 처리되었다고 로깅만 하지만,
		// 실제로는 각 서비스에서 이벤트를 받아 처리하고 결과를 오케스트레이터에게 알려야 함
	}

	private void processPaymentEvent(PaymentSagaEvent event) {
		log.info("Processing payment event: {} for saga: {}", event.getStep(), event.getSagaId());
		// 이벤트를 결제 서비스로 전달
	}

	private void processCancellationEvent(CancellationSagaEvent event) {
		log.info("Processing cancellation event: {} for saga: {}", event.getStep(), event.getSagaId());
		// 이벤트를 취소 처리 서비스로 전달
	}
}
