package com._42195km.msa.competitionservice.infrastructure.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com._42195km.msa.competitionservice.application.event.CompetitionApplicationEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CompetitionApplicationProducer {

	private final KafkaTemplate<String, CompetitionApplicationEvent> kafkaTemplate;
	private static final String TOPIC = "competition_application";

	public CompetitionApplicationProducer(KafkaTemplate<String, CompetitionApplicationEvent> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void sendApplicationEvent(CompetitionApplicationEvent event) {
		String key = event.getCompetitionId().toString() + ":" + event.getParticipantId().toString();
		log.info("Attempting to send event to Kafka: topic={}, key={}, event={}", TOPIC, key, event);

		try {
			kafkaTemplate.send(TOPIC, key, event);
				// .addCallback(
				// success -> log.info("Event sent successfully: offset={}", success.getRecordMetadata().offset()),
				// failure -> log.error("Failed to send event: {}", failure.getMessage(), failure)
				// );
		} catch (Exception e) {
			log.error("Exception occurred while sending event: {}", e.getMessage(), e);
		}
	}
}
