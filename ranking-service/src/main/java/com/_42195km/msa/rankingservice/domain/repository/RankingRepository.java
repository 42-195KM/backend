package com._42195km.msa.rankingservice.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com._42195km.msa.rankingservice.domain.model.Ranking;

public interface RankingRepository {

	void saveAll(List<Ranking> rankings);

	Page<Ranking> findAllWithDetails(Pageable pageable);

	Optional<Ranking> findWithDetails(UUID individualRankingId);
}
