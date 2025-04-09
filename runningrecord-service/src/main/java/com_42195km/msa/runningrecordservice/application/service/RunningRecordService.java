package com_42195km.msa.runningrecordservice.application.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com_42195km.msa.runningrecordservice.application.dto.request.CreateRunningRecordCommandDto;
import com_42195km.msa.runningrecordservice.domain.model.RunningRecord;

public interface RunningRecordService {
	// 러닝 기록 생성
	RunningRecord createRunningRecord(CreateRunningRecordCommandDto createRunningRecordCommandDto);

	// 러닝 기록 조회 by RecordId
	RunningRecord getRecordById(UUID runningRecordId);

	// 전체 러닝 기록 목록 조회
	Page<RunningRecord> getAllRecords(Pageable pageable);

	// 검색 by UserId
	Page<RunningRecord> searchRecords(UUID userId, Pageable pageable);

	// 러닝 기록 삭제
	RunningRecord deleteRecord(UUID runningRecordId);
}
