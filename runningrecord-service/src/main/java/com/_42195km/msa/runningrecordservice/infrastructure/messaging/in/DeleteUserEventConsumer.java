package com._42195km.msa.runningrecordservice.infrastructure.messaging.in;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com._42195km.msa.common.BaseEntity;
import com._42195km.msa.runningrecordservice.domain.model.RunningRecord;
import com._42195km.msa.runningrecordservice.domain.repository.RunningRecordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DeleteUserEventConsumer {
	private final ObjectMapper objectMapper;
	private final RunningRecordRepository runningRecordRepository;

	public DeleteUserEventConsumer(ObjectMapper objectMapper,
		RunningRecordRepository runningRecordRepository) {
		Logger logger = LoggerFactory.getLogger(DeleteUserEventConsumer.class);
		logger.info("consumer created");

		this.objectMapper = objectMapper;
		this.runningRecordRepository = runningRecordRepository;
	}

	@KafkaListener(topics = "delete-user", groupId = "runningrecord-group")
	public void handleDeleteUserEvent(Map<String, Object> eventMap) {
		DeleteUserEventDto deleteUserEventDto = objectMapper.convertValue(eventMap, DeleteUserEventDto.class);
		UUID userId = deleteUserEventDto.getUserId();

		List<RunningRecord> runningRecords = runningRecordRepository.findByUserId(userId);
		runningRecords.forEach(runningRecord -> {
			runningRecord.setDeleted();
			log.info("deleted Running Record: {}", runningRecord.getId());
		});
	}
}
