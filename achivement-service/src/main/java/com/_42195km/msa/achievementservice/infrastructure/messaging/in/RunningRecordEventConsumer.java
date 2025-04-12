package com._42195km.msa.achievementservice.infrastructure.messaging.in;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import com._42195km.msa.achievementservice.application.dto.response.RunningRecordEventDto;
import com._42195km.msa.achievementservice.domain.model.Achievement;
import com._42195km.msa.achievementservice.domain.model.AchievementUser;
import com._42195km.msa.achievementservice.domain.model.CriteriaInequality;
import com._42195km.msa.achievementservice.infrastructure.config.AchievementRunningRecordEvaluator;
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

	@KafkaListener(topics = "create-running-record",
		groupId = "achievement-group"
	)
	public void handleRunningRecordCreateEvent(Map<String, Object> eventMap) {
		Logger logger = LoggerFactory.getLogger(Object.class);

		RunningRecordEventDto runningRecordEventDto = objectMapper.convertValue(eventMap, RunningRecordEventDto.class);
		logEventDto(eventMap, runningRecordEventDto);

		AchievementRunningRecordEvaluator achievementRunningRecordEvaluator = new AchievementRunningRecordEvaluator();

		List<Achievement> achievements = achievementRepository.findAll();
		for (Achievement achievement : achievements) {
			// 이미 달성했는지 조사
			if(achievementRunningRecordEvaluator.isAlreadyAchieved(runningRecordEventDto, achievement)){
				logger.info("Achievement already achieved: {acheivementId: " + achievement.getId() +
							" / userId: " + runningRecordEventDto.getUserId() + "}");
				continue;
			}

			if(achievementRunningRecordEvaluator.isAchievementMet(runningRecordEventDto, achievement)) {
				AchievementUser achievementUser = new AchievementUser(achievement, runningRecordEventDto.getUserId());
				achievement.getAchievementUsers().add(achievementUser);
				achievementUserRepository.save(achievementUser);
			}
		}
	}

	private static void logEventDto(Map<String, Object> eventMap, RunningRecordEventDto runningRecordEventDto) {
		Logger logger = LoggerFactory.getLogger(RunningRecordEventConsumer.class);
		logger.info("Received event: {}", eventMap);

		logger.info("RunningRecord ID: {}", runningRecordEventDto.getId());
		logger.info("User ID: {}", runningRecordEventDto.getUserId());
		logger.info("Distance: {}", runningRecordEventDto.getDistance());
		logger.info("Timer: {}", runningRecordEventDto.getTimer());
		logger.info("Pace: {}", runningRecordEventDto.getPace());
		logger.info("Total Distance: {}", runningRecordEventDto.getTotalDistance());
		logger.info("Total Duration: {}", runningRecordEventDto.getTotalDuration());
		logger.info("Avg Pace: {}", runningRecordEventDto.getAvgPace());
	}

	/**
	 * user_id	UUID	사용자 id
	 * distance	DECIMAL(10, 2)	거리
	 * timer	Duration	시간
	 * pace	DECIMAL(10, 2)	평균 페이스
	 */
}
