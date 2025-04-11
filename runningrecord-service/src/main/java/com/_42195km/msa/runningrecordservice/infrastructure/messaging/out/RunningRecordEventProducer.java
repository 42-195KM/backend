package com._42195km.msa.runningrecordservice.infrastructure.messaging.out;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com._42195km.msa.runningrecordservice.domain.model.RunningRecord;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RunningRecordEventProducer {
	private final KafkaTemplate<String, Object> kafkaTemplate;

	public RunningRecordEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
		Logger logger = LoggerFactory.getLogger(RunningRecordEventProducer.class);
		logger.info("RunningRecordEventProducer started");
		logger.info("kafkaTemplate: {}", kafkaTemplate);
	}

	public void sendRunningRecordCreateEvent(RunningRecord runningRecord) {
		Logger logger = LoggerFactory.getLogger(RunningRecordEventProducer.class);
		logger.info("runningRecord: {}", runningRecord);
		kafkaTemplate.send("create-running-record2", runningRecord.toString());
	}
}
