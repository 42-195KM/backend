package com._42195km.msa.rankingservice.infrastructure.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com._42195km.msa.rankingservice.domain.model.RankingDetail;

public interface RankingDetailJpaRepository extends JpaRepository<RankingDetail, UUID> {
}
