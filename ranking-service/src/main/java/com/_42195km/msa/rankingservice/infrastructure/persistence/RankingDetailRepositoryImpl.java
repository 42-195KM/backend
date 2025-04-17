package com._42195km.msa.rankingservice.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com._42195km.msa.rankingservice.domain.model.RankingDetail;
import com._42195km.msa.rankingservice.domain.repository.RankingDetailRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RankingDetailRepositoryImpl implements RankingDetailRepository {

	private final RankingDetailJpaRepository rankingDetailJpaRepository;

	@Override
	public Page<RankingDetail> findByMetricNameOrderByRank(String keyword, Pageable pageable) {
		return rankingDetailJpaRepository.findByMetricNameOrderByRank(keyword, pageable);
	}
}
