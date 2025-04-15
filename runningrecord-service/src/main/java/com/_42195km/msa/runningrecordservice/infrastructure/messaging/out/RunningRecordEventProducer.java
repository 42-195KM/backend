package com._42195km.msa.runningrecordservice.infrastructure.messaging.out;

import java.time.Duration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com._42195km.msa.runningrecordservice.domain.model.RunningRecord;
import com._42195km.msa.runningrecordservice.domain.repository.RunningRecordRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RunningRecordEventProducer {
	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final RunningRecordRepository runningRecordRepository;
	private final Logger logger = LoggerFactory.getLogger(RunningRecordEventProducer.class);

	public RunningRecordEventProducer(KafkaTemplate<String, Object> kafkaTemplate,
		RunningRecordRepository runningRecordRepository) {
		this.kafkaTemplate = kafkaTemplate;
		this.runningRecordRepository = runningRecordRepository;

		logger.info("RunningRecordEventProducer started");
	}

	public void sendRunningRecordCreateEvent(RunningRecord runningRecord) {
		logger.info("runningRecord: {}", runningRecord);

		RunningRecordEventDto runningRecordEventDto = setRunningRecordEventDto(runningRecord);
		logger.info("runningRecordEventDto: {}", runningRecordEventDto);

		kafkaTemplate.send("create-running-record", runningRecordEventDto);
	}

	public RunningRecordEventDto setRunningRecordEventDto(RunningRecord runningRecord) {
		RunningRecordEventDto runningRecordEventDto = RunningRecordEventDto.from(runningRecord);
		List<RunningRecord> userRecords = runningRecordRepository.findByUserId(runningRecord.getUserId());

		double totalDistance = 0;
		double sumPace = 0;
		Duration totalTimer = Duration.ZERO;

		for(RunningRecord record : userRecords) {
			totalDistance += record.getDistance();
			sumPace += record.getPace();
			totalTimer.plus(record.getTimer());
		}
		double avgPace = sumPace / userRecords.size();

		runningRecordEventDto.setTotalDistance(totalDistance);
		runningRecordEventDto.setTotalTimer(totalTimer);
		runningRecordEventDto.setAvgPace(avgPace);

		return runningRecordEventDto;
	}
}
