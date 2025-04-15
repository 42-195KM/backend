package com._42195km.msa.rankingservice.application.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com._42195km.msa.rankingservice.application.dto.response.CreatePersonalRanking;
import com._42195km.msa.rankingservice.application.dto.response.GetAllPersonalRankingResponseDto;
import com._42195km.msa.rankingservice.application.dto.response.GetPersonalRankingResponseDto;

public interface RankingService {

	CreatePersonalRanking createPersonalRanking();

	Page<GetAllPersonalRankingResponseDto> getAllrankings(Pageable pageable);

	GetPersonalRankingResponseDto getRanking(UUID individualRankingId);
}
