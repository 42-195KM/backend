package com._42195km.msa.auth.infrastructure.messaging.in;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com._42195km.msa.auth.application.exception.AuthException;
import com._42195km.msa.auth.domain.model.Auth;
import com._42195km.msa.auth.infrastructure.persistence.AuthRepositoryImpl;
import com._42195km.msa.common.aop.AuditingKafkaListener;
import com._42195km.msa.common.config.AuditorAwareImpl;
import com._42195km.msa.common.exception.CustomBusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventConsumer {

	private final ObjectMapper objectMapper;
	private final AuthRepositoryImpl authRepositoryImpl;
	private final Logger logger = LoggerFactory.getLogger(UserEventConsumer.class);

	@KafkaListener(topics = "create-user", groupId = "${spring.kafka.consumer.group-id}")
	@AuditingKafkaListener
	@Transactional
	public void handleCreateUserEvent(Map<String, Object> event) {
		try {

			logger.info("Received event: {}", event);
			CreateUserEventDto createUserEventDto = objectMapper.convertValue(event, CreateUserEventDto.class);

			Auth auth = CreateUserEventDto.toAuth(createUserEventDto);

			authRepositoryImpl.save(auth);
			logger.info("Auth-User Sync Success: {}", auth.getUserUuid());
		} catch (Exception e) {
			log.error("Error handling event: {}", e.getMessage(), e);
			throw e;
		} finally {
			AuditorAwareImpl.clear();
		}
	}

	@KafkaListener(topics = "update-user", groupId = "${spring.kafka.consumer.group-id}")
	@AuditingKafkaListener
	@Transactional
	public void handleUpdateUserEvent(Map<String, Object> event) {
		try {

			UpdateUserEventDto updateUserEventDto = objectMapper.convertValue(event, UpdateUserEventDto.class);

			Auth targetAuth = authRepositoryImpl.findByUserUuid(updateUserEventDto.getUserId())
				.orElseThrow(() -> CustomBusinessException.from(AuthException.NOT_FOUND_AUTH_USER));

			targetAuth.update(updateUserEventDto);

		} catch (Exception e) {
			log.error("Error handling event: {}", e.getMessage(), e);
		}
	}

	@KafkaListener(topics = "delete-user", groupId = "${spring.kafka.consumer.group-id}")
	@AuditingKafkaListener
	@Transactional
	public void handleDeleteUserEvent(Map<String, Object> event) {

		try {
			DeleteUserEventDto deleteUserEventDto = objectMapper.convertValue(event, DeleteUserEventDto.class);

			Auth targetAuth = authRepositoryImpl.findByUserUuid(deleteUserEventDto.getUserId())
				.orElseThrow(() -> CustomBusinessException.from(AuthException.NOT_FOUND_AUTH_USER));

			targetAuth.setDeleted();

		} catch (Exception e) {
			log.error("Error handling event: {}", e.getMessage(), e);
		} finally {
			AuditorAwareImpl.clear();
		}
	}

}
