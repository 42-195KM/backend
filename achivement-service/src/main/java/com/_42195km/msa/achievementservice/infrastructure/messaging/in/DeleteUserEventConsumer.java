package com._42195km.msa.achievementservice.infrastructure.messaging.in;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com._42195km.msa.achievementservice.domain.model.AchievementUser;
import com._42195km.msa.achievementservice.domain.repository.AchievementRepository;
import com._42195km.msa.achievementservice.domain.repository.AchievementUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DeleteUserEventConsumer {
	private final ObjectMapper objectMapper;
	private final AchievementUserRepository achievementUserRepository;

	public DeleteUserEventConsumer(ObjectMapper objectMapper,
		AchievementUserRepository achievementUserRepository) {
		Logger logger = LoggerFactory.getLogger(DeleteUserEventConsumer.class);
		logger.info("consumer created");

		this.objectMapper = objectMapper;
		this.achievementUserRepository = achievementUserRepository;
	}

	@KafkaListener(topics = "delete-user", groupId = "runningrecord-group")
	public void handleDeleteUserEvent(Map<String, Object> eventMap) {
		Logger logger = LoggerFactory.getLogger(DeleteUserEventConsumer.class);

		DeleteUserEventDto deleteUserEventDto = objectMapper.convertValue(eventMap, DeleteUserEventDto.class);
		UUID userId = deleteUserEventDto.getUserId();

		List<AchievementUser> achievementUsers = achievementUserRepository.findByUserId(userId);
		achievementUsers.forEach(achievementUser -> {
			achievementUser.setDeleted();
			logger.info("deleted user {}", achievementUser.getUserId());
		});
	}
}
