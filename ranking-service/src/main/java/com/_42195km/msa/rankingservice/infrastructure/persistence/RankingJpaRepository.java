package com._42195km.msa.rankingservice.infrastructure.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com._42195km.msa.rankingservice.domain.model.Ranking;

public interface RankingJpaRepository extends JpaRepository<Ranking, UUID> {
}
