package com._42195km.msa.achievementservice.infrastructure.messaging.in;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import com._42195km.msa.achievementservice.domain.model.Achievement;
import com._42195km.msa.achievementservice.domain.model.AchievementUser;
import com._42195km.msa.achievementservice.infrastructure.evaluator.AchievementRunningRecordEvaluator;
import com._42195km.msa.achievementservice.infrastructure.messaging.out.AchieveEventProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com._42195km.msa.achievementservice.domain.repository.AchievementRepository;
import com._42195km.msa.achievementservice.domain.repository.AchievementUserRepository;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

//로그를 찍어서 해보셈
@Slf4j
@Component
public class RunningRecordEventConsumer {

	private final ObjectMapper objectMapper;
	private final AchievementRepository achievementRepository;
	private final AchievementUserRepository achievementUserRepository;
	private final AchieveEventProducer achieveEventProducer;

	public RunningRecordEventConsumer(
		AchievementUserRepository achievementUserRepository,
		AchievementRepository achievementRepository,
		AchieveEventProducer achieveEventProducer)
	{
		Logger logger = LoggerFactory.getLogger(RunningRecordEventConsumer.class);
		logger.info("consumer created");
		this.objectMapper = new ObjectMapper()
			.registerModule(new JavaTimeModule())
			.disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
		this.achievementUserRepository = achievementUserRepository;
		this.achievementRepository = achievementRepository;
		this.achieveEventProducer = achieveEventProducer;
	}

	@Transactional
	@KafkaListener(topics = "create-running-record",
		groupId = "achievement-group"
	)
	public void handleRunningRecordCreateEvent(String eventJson){
		Logger logger = LoggerFactory.getLogger(Object.class);

		// objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		// RunningRecordEventDto runningRecordEventDto = objectMapper.convertValue(eventMap, RunningRecordEventDto.class);
		// logEventDto(eventMap, runningRecordEventDto);

		try {
			RunningRecordEventDto runningRecordEventDto = objectMapper.readValue(eventJson, RunningRecordEventDto.class);

			AchievementRunningRecordEvaluator achievementRunningRecordEvaluator = new AchievementRunningRecordEvaluator();

			boolean isAnyAchieved = false;
			List<Achievement> achievements = achievementRepository.findAll();
			for (Achievement achievement : achievements) {
				// 이미 달성했는지 조사
				if(achievementRunningRecordEvaluator.isAlreadyAchieved(runningRecordEventDto, achievement)){
					isAnyAchieved = true;
					logger.info("Achievement already achieved: {acheivementId: " + achievement.getId() +
						" / userId: " + runningRecordEventDto.getUserId() + "}");
					continue;
				}

				if(achievementRunningRecordEvaluator.isAchievementMet(runningRecordEventDto, achievement)) {
					isAnyAchieved = true;
					AchievementUser achievementUser = new AchievementUser(achievement, runningRecordEventDto.getUserId());
					achievement.getAchievementUsers().add(achievementUser);
					achievementUserRepository.save(achievementUser);
					achieveEventProducer.sendAchievementEvent(achievementUser);
				}
			}

			if(!isAnyAchieved){
				logger.info("Achievement not achieved");
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
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
		logger.info("Total Duration: {}", runningRecordEventDto.getTotalTimer());
		logger.info("Avg Pace: {}", runningRecordEventDto.getAvgPace());
	}
}
