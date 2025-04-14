package com._42195km.msa.achievementservice.infrastructure.messaging.out;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com._42195km.msa.achievementservice.domain.model.AchievementUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AchieveEventProducer {
	private KafkaTemplate<String, Object> kafkaTemplate;
	private Logger logger = LoggerFactory.getLogger(AchieveEventProducer.class);

	public AchieveEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
		logger.info("AchieveEventProducer created");
	}

	public void sendAchievementEvent(AchievementUser achievementUser) {
		logger.info("AchievementUser: {}", achievementUser);

		// TODO: producer에서 user 정보 API를 feignClient로 요청해서 mediaId를 받아오기
		AchieveEventDto achieveEventDto = AchieveEventDto.from(achievementUser);

		logger.info("AchieveEventDto: {}", achieveEventDto);

		kafkaTemplate.send("achieve-achievement", achieveEventDto);
	}
}
