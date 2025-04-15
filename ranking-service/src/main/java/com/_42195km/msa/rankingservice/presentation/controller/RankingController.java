package com._42195km.msa.rankingservice.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._42195km.msa.common.aop.CheckPermission;
import com._42195km.msa.common.api.ApiResponse;
import com._42195km.msa.rankingservice.application.dto.response.CreatePersonalRanking;
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
	@CheckPermission(roles = {"MASTER"}, mode = CheckPermission.Mode.ANY)
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
}
