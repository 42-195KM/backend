package com._42195km.msa.rankingservice.infrastructure.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com._42195km.msa.rankingservice.domain.model.Ranking;
import com._42195km.msa.rankingservice.domain.repository.RankingRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RankingRepositoryImpl implements RankingRepository {

	private final RankingJpaRepository rankingJpaRepository;

	@Override
	@Transactional
	public void saveAll(List<Ranking> rankings) {

		rankingJpaRepository.saveAll(rankings);
	}
}
