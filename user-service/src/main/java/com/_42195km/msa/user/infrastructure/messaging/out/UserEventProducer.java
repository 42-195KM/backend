package com._42195km.msa.user.infrastructure.messaging.out;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com._42195km.msa.common.config.KafkaEventSender;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserEventProducer {

	private final KafkaEventSender kafkaEventSender;
	private final Logger logger = LoggerFactory.getLogger(UserEventProducer.class);

	public UserEventProducer(KafkaEventSender kafkaEventSender) {
		this.kafkaEventSender = kafkaEventSender;
		logger.info("Created DeleteUserEventProducer");
	}

	public void sendUserEvent(Object event, UserEventType eventType) {

		// Enum타입으로 분리 -> Delete는 따로 빼기
		switch (eventType) {
			case CREATE -> sendCreateUserEvent((UserEventDto)event);
			case UPDATE -> sendUpdateUserEvent((UserEventDto)event);
			case DELETE -> sendDeleteUserEvent((UUID)event);
			default -> throw new IllegalArgumentException("타입을 입력해 주세요!");
		}
	}

	private void sendCreateUserEvent(UserEventDto userEventDto) {
		logger.info("created user {}", userEventDto.getUserId());
		kafkaEventSender.send("create-user", userEventDto);
	}

	private void sendUpdateUserEvent(UserEventDto userEventDto) {
		logger.info("updated user {}", userEventDto.getUserId());
		kafkaEventSender.send("update-user", userEventDto);
	}

	public void sendDeleteUserEvent(UUID userId) {
		logger.info("deleted user {}", userId);
		DeleteUserEventDto deleteUserEventDto = new DeleteUserEventDto(userId);
		kafkaEventSender.send("delete-user", deleteUserEventDto);
	}
}
