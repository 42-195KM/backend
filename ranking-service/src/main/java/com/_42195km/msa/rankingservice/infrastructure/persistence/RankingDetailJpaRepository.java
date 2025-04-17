package com._42195km.msa.rankingservice.infrastructure.persistence;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com._42195km.msa.rankingservice.domain.model.RankingDetail;

public interface RankingDetailJpaRepository extends JpaRepository<RankingDetail, UUID> {

	@Query(
		value = """
			SELECT d
			FROM RankingDetail d
			JOIN FETCH d.ranking r
			WHERE d.metricName LIKE %:keyword% AND r.isDeleted = false
			ORDER BY d.rank ASC
			""",
		countQuery = """
			SELECT COUNT(d)
			FROM RankingDetail d
			JOIN d.ranking r
			WHERE d.metricName LIKE %:keyword% AND r.isDeleted = false
			"""
	)
	Page<RankingDetail> findByMetricNameOrderByRank(@Param("keyword") String keyword, Pageable pageable);
}
