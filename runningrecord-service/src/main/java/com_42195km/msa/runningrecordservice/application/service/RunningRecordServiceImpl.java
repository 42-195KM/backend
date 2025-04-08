package com_42195km.msa.runningrecordservice.application.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
		return null;
	}

	@Override
	public Page<RunningRecord> getAllRecords(Pageable pageable) {
		return null;
	}

	@Override
	public Page<RunningRecord> searchRecords(Pageable pageable, UUID userId) {
		return null;
	}

	@Override
	public void deleteRecord(UUID runningRecordId) {

	}
}
