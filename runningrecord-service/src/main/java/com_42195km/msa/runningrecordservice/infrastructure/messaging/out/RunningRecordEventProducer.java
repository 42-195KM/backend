package com_42195km.msa.runningrecordservice.infrastructure.messaging.out;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com_42195km.msa.runningrecordservice.domain.model.RunningRecord;

@Component
public class RunningRecordEventProducer {
	private final KafkaTemplate<String, Object> kafkaTemplate;

	public RunningRecordEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void sendRunningRecordCreateEvent(RunningRecord runningRecord) {
		kafkaTemplate.send("create-running-record", runningRecord);
	}
}
