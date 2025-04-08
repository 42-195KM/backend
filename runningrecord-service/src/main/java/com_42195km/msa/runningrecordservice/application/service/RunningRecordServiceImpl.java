package com_42195km.msa.runningrecordservice.application.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com_42195km.msa.runningrecordservice.application.dto.request.CreateRunningRecordCommandDto;
import com_42195km.msa.runningrecordservice.domain.model.RunningRecord;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RunningRecordServiceImpl implements RunningRecordService {

	@Override
	public RunningRecord createRunningRecord(CreateRunningRecordCommandDto createRunningRecordCommandDto) {
		return null;
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
