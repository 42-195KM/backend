package com._42195km.msa.rankingservice.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com._42195km.msa.rankingservice.domain.model.RankingDetail;

public interface RankingDetailRepository {

	Page<RankingDetail> findByMetricNameOrderByRank(String keyword, Pageable pageable);
}
