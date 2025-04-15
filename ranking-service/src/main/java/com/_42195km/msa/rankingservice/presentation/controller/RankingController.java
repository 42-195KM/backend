package com._42195km.msa.rankingservice.presentation.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._42195km.msa.common.aop.CheckPermission;
import com._42195km.msa.common.api.ApiResponse;
import com._42195km.msa.rankingservice.application.dto.response.CreatePersonalRanking;
import com._42195km.msa.rankingservice.application.dto.response.GetAllPersonalRankingResponseDto;
import com._42195km.msa.rankingservice.application.service.RankingServiceImpl;
import com._42195km.msa.rankingservice.application.success.RankingSuccessCode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "랭킹 서비스", description = "랭킹 컨트롤러")
public class RankingController {

	private final RankingServiceImpl rankingServiceImpl;

	@PostMapping("/v1/app/indiviudal-rankings")
	@Operation(summary = "일정 스케쥴링에 따라 개인 기록 생성", description = "생성은 'MASTER' 만 가능")
	@CheckPermission(roles = {"MASTER"}, mode = CheckPermission.Mode.ALL)
	public ResponseEntity<ApiResponse<CreatePersonalRanking>> createPersonalRanking() {

		CreatePersonalRanking createPersonalRanking = rankingServiceImpl.createPersonalRanking();

		return ResponseEntity
			.ok(
				ApiResponse
					.<CreatePersonalRanking>builder()
					.code(RankingSuccessCode.PERSONAL_RANKING_SCHEDULING.getCode())
					.message(RankingSuccessCode.PERSONAL_RANKING_SCHEDULING.getMessage())
					.status(RankingSuccessCode.PERSONAL_RANKING_SCHEDULING.getStatus())
					.data(null)
					.build()
			);
	}

	@GetMapping("/v1/indiviudal-rankings")
	@Operation(summary = "개인 랭킹 목록 보기", description = "조회는 'NORMAL' 만 가능")
	@CheckPermission(roles = {"NORMAL"}, mode = CheckPermission.Mode.ALL)
	public ResponseEntity<ApiResponse<Page<GetAllPersonalRankingResponseDto>>> getAllPersonalRanking(
		@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable
	) {
		Page<GetAllPersonalRankingResponseDto> allrankings = rankingServiceImpl.getAllrankings(pageable);

		return ResponseEntity.ok(
			ApiResponse
				.<Page<GetAllPersonalRankingResponseDto>>builder()
				.code(RankingSuccessCode.PERSONAL_RANKING_ALL_SEARCH_SUCCESS.getCode())
				.message(RankingSuccessCode.PERSONAL_RANKING_ALL_SEARCH_SUCCESS.getMessage())
				.status(RankingSuccessCode.PERSONAL_RANKING_ALL_SEARCH_SUCCESS.getStatus())
				.data(allrankings)
				.build()
		);
	}
}
