package com_42195km.msa.runningrecordservice.application.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com._42195km.msa.common.BaseEntity;
import com._42195km.msa.common.exception.CustomBusinessException;

import com_42195km.msa.runningrecordservice.application.dto.request.CreateRunningRecordCommandDto;
import com_42195km.msa.runningrecordservice.domain.model.RunningRecord;
import com_42195km.msa.runningrecordservice.domain.repository.RunningRecordRepository;
import com_42195km.msa.runningrecordservice.infrastructure.config.RunningRecordServiceCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RunningRecordServiceImpl implements RunningRecordService {

	RunningRecordRepository runningRecordRepository;

	@Override
	public RunningRecord createRunningRecord(CreateRunningRecordCommandDto createRunningRecordCommandDto) {
		try {
			RunningRecord runningRecord = RunningRecord.createRunningRecord(createRunningRecordCommandDto);
			runningRecord = runningRecordRepository.save(runningRecord);
			return runningRecord;
		}
		catch (Exception e) {
			throw CustomBusinessException.from(RunningRecordServiceCode.RUNNING_RECORD_CREATE_FAIL);
		}
	}

	@Override
	public RunningRecord getRecordById(UUID runningRecordId) {
		try {
			Optional<RunningRecord> runningRecord = runningRecordRepository.findById(runningRecordId);
			return runningRecord.orElseThrow(() ->
				CustomBusinessException.from(RunningRecordServiceCode.RUNNING_RECORD_GET_FAIL));
		}
		catch (Exception e) {
			throw CustomBusinessException.from(RunningRecordServiceCode.RUNNING_RECORD_GET_FAIL);
		}
	}

	@Override
	public Page<RunningRecord> getAllRecords(Pageable pageable) {
		try {
			return runningRecordRepository.findAll(pageable);
		}
		catch (Exception e) {
			throw CustomBusinessException.from(RunningRecordServiceCode.RUNNING_RECORD_GET_ALL_FAIL);
		}
	}

	@Override
	public Page<RunningRecord> searchRecords(UUID userId, LocalDateTime createdAt, Pageable pageable) {
		try{
			return runningRecordRepository.searchByUserId(userId, createdAt, pageable);
		} catch (Exception e) {
			throw CustomBusinessException.from(RunningRecordServiceCode.RUNNING_RECORD_SEARCH_FAIL);
		}
	}

	@Override
	public RunningRecord deleteRecord(UUID runningRecordId) {
		try{
			Optional<RunningRecord> runningRecord = runningRecordRepository.findById(runningRecordId);
			runningRecord.ifPresent(BaseEntity::setDeleted);
			return runningRecord.orElseThrow(() ->
				CustomBusinessException.from(RunningRecordServiceCode.RUNNING_RECORD_DELETE_FAIL));
		} catch (Exception e) {
			throw CustomBusinessException.from(RunningRecordServiceCode.RUNNING_RECORD_DELETE_FAIL);
		}
	}
}
