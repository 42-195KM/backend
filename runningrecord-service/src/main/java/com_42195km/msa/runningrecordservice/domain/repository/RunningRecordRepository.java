package com_42195km.msa.runningrecordservice.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com_42195km.msa.runningrecordservice.domain.model.RunningRecord;

public interface RunningRecordRepository {
	RunningRecord save(RunningRecord runningRecord);
	Optional<RunningRecord> findById(UUID runningRecordId);
	Page<RunningRecord> findAll(Pageable pageable);
	Page<RunningRecord> searchByUserId(UUID userId, Pageable pageable);
}
