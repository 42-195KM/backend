package com._42195km.msa.competitionservice.infrastructure.persistence;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com._42195km.msa.competitionservice.domain.model.Competition;

public interface CompetitionJpaRepository extends JpaRepository <Competition, UUID>{

	Page<Competition> findByIsDeletedFalse(Pageable pageable);

	@Query("SELECT c FROM Competition c WHERE c.isDeleted = false AND c.title LIKE CONCAT('%', :keyword, '%')")
	Page<Competition> searchByTitle(@Param("keyword") String keyword, Pageable pageable);

	@Query("SELECT c FROM Competition c WHERE c.isDeleted = false AND " +
		"(CAST(c.type AS string) = :keyword OR CAST(c.receptionType AS string) = :keyword)")
	Page<Competition> searchByEnumType(@Param("keyword") String keyword, Pageable pageable);

	Page<Competition> findByUserIdAndIsDeletedFalse(UUID userId, Pageable pageable);
}
