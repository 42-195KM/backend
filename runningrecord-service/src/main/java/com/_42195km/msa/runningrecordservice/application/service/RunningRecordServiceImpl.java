package com._42195km.msa.runningrecordservice.application.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com._42195km.msa.common.BaseEntity;
import com._42195km.msa.common.exception.CustomBusinessException;

import com._42195km.msa.common.service.ServiceExecutor;
import com._42195km.msa.runningrecordservice.application.dto.request.CreateRunningRecordCommandDto;
import com._42195km.msa.runningrecordservice.domain.model.RunningRecord;
import com._42195km.msa.runningrecordservice.domain.repository.RunningRecordRepository;
import com._42195km.msa.runningrecordservice.infrastructure.code.RunningRecordServiceCode;
import com._42195km.msa.runningrecordservice.infrastructure.messaging.out.RunningRecordEventProducer;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RunningRecordServiceImpl extends ServiceExecutor implements RunningRecordService {

	private final RunningRecordRepository runningRecordRepository;
	private final RunningRecordEventProducer runningRecordEventProducer;

	@Transactional
	@Override
	public RunningRecord createRunningRecord(CreateRunningRecordCommandDto createRunningRecordCommandDto) {
		return execute(() -> {
			RunningRecord runningRecord = RunningRecord.createRunningRecord(createRunningRecordCommandDto);
			runningRecord = runningRecordRepository.save(runningRecord);
			runningRecordEventProducer.sendRunningRecordCreateEvent(runningRecord);
			return runningRecord;
		}, RunningRecordServiceCode.RUNNING_RECORD_CREATE_FAIL);
	}

	@Override
	public RunningRecord getRecordById(UUID runningRecordId) {
		return execute(() -> {
			Optional<RunningRecord> runningRecord = runningRecordRepository.findById(runningRecordId);
			return runningRecord.orElseThrow(() ->
				CustomBusinessException.from(RunningRecordServiceCode.RUNNING_RECORD_GET_FAIL));
		}, RunningRecordServiceCode.RUNNING_RECORD_GET_FAIL);
	}

	@Override
	public Page<RunningRecord> getAllRecords(Pageable pageable) {
		return execute(() ->
			runningRecordRepository.findAll(pageable)
			, RunningRecordServiceCode.RUNNING_RECORD_GET_ALL_FAIL);
	}

	@Override
	public Page<RunningRecord> searchRecords(UUID userId, LocalDateTime createdAt, Pageable pageable) {
		return execute(() ->
			runningRecordRepository.searchByUserId(userId, createdAt, pageable)
			, RunningRecordServiceCode.RUNNING_RECORD_SEARCH_FAIL);
	}

	@Transactional
	@Override
	public RunningRecord deleteRecord(UUID runningRecordId) {
		return execute(() -> {
			Optional<RunningRecord> runningRecord = runningRecordRepository.findById(runningRecordId);
			runningRecord.ifPresent(BaseEntity::setDeleted);
			return runningRecord.orElseThrow(() ->
				CustomBusinessException.from(RunningRecordServiceCode.RUNNING_RECORD_DELETE_FAIL));
		}, RunningRecordServiceCode.RUNNING_RECORD_DELETE_FAIL);
	}
}
