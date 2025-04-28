package com._42195km.msa.runningrecordservice.infrastructure.messaging.out;

import java.time.Duration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com._42195km.msa.runningrecordservice.domain.model.RunningRecord;
import com._42195km.msa.runningrecordservice.domain.model.RunningRecordStats;
import com._42195km.msa.runningrecordservice.domain.repository.RunningRecordRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RunningRecordEventProducer {
	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final RunningRecordRepository runningRecordRepository;
	private final Logger logger = LoggerFactory.getLogger(RunningRecordEventProducer.class);
	private final ObjectMapper objectMapper;

	public RunningRecordEventProducer(KafkaTemplate<String, Object> kafkaTemplate,
		RunningRecordRepository runningRecordRepository) {
		this.kafkaTemplate = kafkaTemplate;
		this.runningRecordRepository = runningRecordRepository;
		this.objectMapper = new ObjectMapper()
			.registerModule(new JavaTimeModule())
			.disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);

		logger.info("RunningRecordEventProducer started");
	}

	public void sendRunningRecordCreateEvent(RunningRecord runningRecord) {
		logger.info("runningRecord: {}", runningRecord);

		RunningRecordEventDto runningRecordEventDto = setRunningRecordEventDto(runningRecord);

		logger.info("runningRecordEventDto: {}", runningRecordEventDto);

		try{
			String json = objectMapper.writeValueAsString(runningRecordEventDto);
			kafkaTemplate.send("create-running-record", json);
		} catch (JsonProcessingException e) {
			logger.error("Failed to serialize RunningRecordEventDto", e);
		}

	}

	public RunningRecordEventDto setRunningRecordEventDto(RunningRecord runningRecord) {
		RunningRecordEventDto runningRecordEventDto = RunningRecordEventDto.from(runningRecord);

		RunningRecordStats userStats = runningRecordRepository.findUserStatsByUserId(runningRecord.getUserId());
		double totalDistance = userStats.getTotalDistance();
		long totalTimerSeconds = userStats.getTotalTimerSeconds().longValue();
		Duration totalTimer = Duration.ofNanos(totalTimerSeconds);
		double avgPace = userStats.getAvgPace();

		runningRecordEventDto.setTotalDistance(totalDistance);
		runningRecordEventDto.setTotalTimer(totalTimer);
		runningRecordEventDto.setAvgPace(avgPace);

		return runningRecordEventDto;
	}
}
