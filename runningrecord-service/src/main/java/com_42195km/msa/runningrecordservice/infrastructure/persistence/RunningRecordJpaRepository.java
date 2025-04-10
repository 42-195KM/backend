package com_42195km.msa.runningrecordservice.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com_42195km.msa.runningrecordservice.domain.model.RunningRecord;
import com_42195km.msa.runningrecordservice.domain.repository.RunningRecordRepository;

@Repository
public interface RunningRecordJpaRepository extends RunningRecordRepository, JpaRepository<RunningRecord, UUID> {
	@Override
	default Optional<RunningRecord> findById(UUID runningRecordId){
		return findByIdAndIsDeletedFalse(runningRecordId);
	}

	Optional<RunningRecord> findByIdAndIsDeletedFalse(UUID runningRecordId);

	@Override
	@Query("SELECT r FROM RunningRecord r "
		+ "WHERE r.userId = :userId "
		+ "AND r.isDeleted = false "
		+ "AND r.createdAt BETWEEN :createdAt AND CURRENT_TIMESTAMP"
	)
	Page<RunningRecord> searchByUserId(
		@Param("userId") UUID userId,
		@Param("createdAt") LocalDateTime createdAt,
		Pageable pageable);

}
