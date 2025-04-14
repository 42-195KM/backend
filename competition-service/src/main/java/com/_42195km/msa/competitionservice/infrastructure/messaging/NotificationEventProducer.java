package com._42195km.msa.competitionservice.infrastructure.messaging;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventProducer {

	private final KafkaTemplate<String, CompetitionApplyNotificationDto> kafkaTemplate;

	public void sendNotification(CompetitionApplyNotificationDto notificationEventDto) {
		try {
			kafkaTemplate.send("competition_notification", notificationEventDto.getUserId().toString(), notificationEventDto);
			log.info("Notification event sent for userId: {}", notificationEventDto.getUserId());
		} catch (Exception e) {
			log.error("Failed to send notification event: {}", e.getMessage(), e);
			throw e;
		}
	}

}
