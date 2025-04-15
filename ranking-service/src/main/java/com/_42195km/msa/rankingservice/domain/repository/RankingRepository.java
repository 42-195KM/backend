package com._42195km.msa.rankingservice.domain.repository;

import java.util.List;

import com._42195km.msa.rankingservice.domain.model.Ranking;

public interface RankingRepository {
	
	void saveAll(List<Ranking> rankings);
}
