package com._42195km.msa.user.infrastructure.messaging.out;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DeleteUserEventProducer {
	private final KafkaTemplate<String, Object> kafkaTemplate;
	private Logger logger = LoggerFactory.getLogger(DeleteUserEventProducer.class);

	public DeleteUserEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
		logger.info("Created DeleteUserEventProducer");
	}

	public void sendDeleteUserEvent(UUID userId) {
		logger.info("deleted user {}", userId);
		DeleteUserEventDto deleteUserEventDto = new DeleteUserEventDto(userId);
		kafkaTemplate.send("delete-user", deleteUserEventDto);
	}
}
