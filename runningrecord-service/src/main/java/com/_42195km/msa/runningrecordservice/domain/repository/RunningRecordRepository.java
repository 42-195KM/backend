package com._42195km.msa.runningrecordservice.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com._42195km.msa.runningrecordservice.domain.model.RunningRecord;
import com._42195km.msa.runningrecordservice.domain.model.RunningRecordStats;

public interface RunningRecordRepository {
	RunningRecord save(RunningRecord runningRecord);
	Optional<RunningRecord> findById(UUID runningRecordId);
	Page<RunningRecord> findAll(Pageable pageable);
	Page<RunningRecord> searchByUserId(UUID userId, LocalDateTime createdAt, Pageable pageable);
	List<RunningRecord> findByUserId(UUID userId);
	RunningRecordStats findUserStatsByUserId(UUID userId);
}
