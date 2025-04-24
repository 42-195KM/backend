package com._42195km.msa.achievementservice.infrastructure.messaging.out;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com._42195km.msa.achievementservice.domain.model.AchievementUser;
import com._42195km.msa.achievementservice.infrastructure.client.UserClient;
import com._42195km.msa.achievementservice.infrastructure.client.UserMediaIdDto;
import com._42195km.msa.common.api.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AchieveEventProducer {
	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final UserClient userClient;
	private Logger logger = LoggerFactory.getLogger(AchieveEventProducer.class);

	public AchieveEventProducer(KafkaTemplate<String, Object> kafkaTemplate,
		UserClient userClient) {
		this.kafkaTemplate = kafkaTemplate;
		this.userClient = userClient;
		logger.info("AchieveEventProducer created");
	}

	public void sendAchievementEvent(AchievementUser achievementUser) {
		logger.info("AchievementUser: {}", achievementUser);

		AchieveEventDto achieveEventDto = AchieveEventDto.from(achievementUser);

		ResponseEntity<ApiResponse<UserMediaIdDto>> responseEntity = userClient.getUser(achievementUser.getUserId());

		if(responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
			UserMediaIdDto userMediaIdDto = responseEntity.getBody().data();
			achieveEventDto.setUserMediaId(userMediaIdDto.getUserMediaId());
		}
		else {
			throw new RuntimeException("Error sending achievement event");
		}

		logger.info("AchieveEventDto: {}", achieveEventDto);

		kafkaTemplate.send("achieve-achievement", achieveEventDto);
	}
}
