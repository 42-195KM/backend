package com._42195km.msa.rankingservice.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import com._42195km.msa.rankingservice.domain.repository.RankingDetailRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RankingDetailRepositoryImpl implements RankingDetailRepository {

	private final RankingDetailJpaRepository rankingDetailJpaRepository;
}
