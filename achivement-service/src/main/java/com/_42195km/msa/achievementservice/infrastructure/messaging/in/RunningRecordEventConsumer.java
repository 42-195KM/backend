package com._42195km.msa.achievementservice.infrastructure.messaging.in;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import com._42195km.msa.achievementservice.domain.repository.AchievementRepository;
import com._42195km.msa.achievementservice.domain.repository.AchievementUserRepository;

import lombok.extern.slf4j.Slf4j;

//로그를 찍어서 해보셈
@Slf4j
@Component
public class RunningRecordEventConsumer {

	private final ObjectMapper objectMapper;
	private final AchievementRepository achievementRepository;
	private final AchievementUserRepository achievementUserRepository;

	public RunningRecordEventConsumer(ObjectMapper objectMapper, AchievementUserRepository achievementUserRepository, AchievementRepository achievementRepository) {
		Logger logger = LoggerFactory.getLogger(RunningRecordEventConsumer.class);
		logger.info("consumer created");
		this.objectMapper = objectMapper;
		this.achievementUserRepository = achievementUserRepository;
		this.achievementRepository = achievementRepository;
	}

	@KafkaListener(topics = "create-running-record2",
		groupId = "achievement-group"
	)
	public void handleRunningRecordCreateEvent(String event) {
		Logger logger = LoggerFactory.getLogger(RunningRecordEventConsumer.class);
		// logger.info("Received event: {}", eventMap);
		//
		// for(Map.Entry<String, Object> entry : eventMap.entrySet()) {
		// 	String key = entry.getKey();
		// 	Object value = entry.getValue();
		// 	logger.info("Received key: {}", key);
		// 	logger.info("Received value: {}", value);
		// }
		logger.info(event);
	}

	/**
	 * user_id	UUID	사용자 id
	 * distance	DECIMAL(10, 2)	거리
	 * timer	Duration	시간
	 * pace	DECIMAL(10, 2)	평균 페이스
	 */
}
