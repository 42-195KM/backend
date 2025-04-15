package com._42195km.msa.rankingservice.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com._42195km.msa.rankingservice.domain.model.Ranking;

public interface RankingJpaRepository extends JpaRepository<Ranking, UUID> {

	@Query(
		value = "SELECT DISTINCT r FROM Ranking r LEFT JOIN FETCH r.details WHERE r.isDeleted = false",
		countQuery = "SELECT COUNT(r) FROM Ranking r WHERE r.isDeleted = false"
	)
	Page<Ranking> findAllWithDetalis(Pageable pageable);

	@Query("SELECT r FROM Ranking r LEFT JOIN FETCH r.details WHERE r.identifierId = :identifierId AND r.isDeleted = false")
	Optional<Ranking> finWithDetails(UUID identifierId);

	Optional<Ranking> findByIdentifierIdAndIsDeletedFalse(UUID individualRankingId);
}
