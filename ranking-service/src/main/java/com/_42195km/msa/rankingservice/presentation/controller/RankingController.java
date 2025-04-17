package com._42195km.msa.rankingservice.presentation.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com._42195km.msa.common.aop.CheckPermission;
import com._42195km.msa.common.api.ApiResponse;
import com._42195km.msa.rankingservice.application.dto.response.CreatePersonalRanking;
import com._42195km.msa.rankingservice.application.dto.response.DeletePersonalRankingResponseDto;
import com._42195km.msa.rankingservice.application.dto.response.GetAllPersonalRankingResponseDto;
import com._42195km.msa.rankingservice.application.dto.response.GetPersonalRankingResponseDto;
import com._42195km.msa.rankingservice.application.dto.response.RankingDetailResponseDto;
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

	@PostMapping("/v1/app/individual-rankings")
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

	@GetMapping("/v1/individual-rankings")
	@Operation(summary = "개인 랭킹 목록 모든 보기", description = "조회는 'MASTER' 만 가능")
	@CheckPermission(roles = {"MASTER"}, mode = CheckPermission.Mode.ALL)
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

	@GetMapping("/v1/individual-rankings/{individualRankingId}")
	@Operation(summary = "개인 랭킹 목록 단건 보기", description = "조회는 'NORMAL' 만 가능")
	@CheckPermission(roles = {"NORMAL"}, mode = CheckPermission.Mode.ALL)
	public ResponseEntity<ApiResponse<GetPersonalRankingResponseDto>> getPersonalRanking(
		@PathVariable UUID individualRankingId
	) {

		GetPersonalRankingResponseDto getPersonalRankingResponseDto = rankingServiceImpl.getRanking(
			individualRankingId);

		return ResponseEntity.ok(
			ApiResponse
				.<GetPersonalRankingResponseDto>builder()
				.code(RankingSuccessCode.PERSONAL_RANKING_SEARCH_SUCCESS.getCode())
				.message(RankingSuccessCode.PERSONAL_RANKING_SEARCH_SUCCESS.getMessage())
				.status(RankingSuccessCode.PERSONAL_RANKING_SEARCH_SUCCESS.getStatus())
				.data(getPersonalRankingResponseDto)
				.build()
		);
	}

	@GetMapping("/v1/individual-rankings/search")
	@Operation(summary = "랭킹 키워드 검색", description = "랭킹 조회는 'MASTER' 만 가능'")
	@CheckPermission(roles = {"MASTER", "NORMAL"}, mode = CheckPermission.Mode.ANY)
	public ResponseEntity<ApiResponse<Page<RankingDetailResponseDto>>> getAllPersonalRankings(
		@RequestParam String keyword,
		@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable
	) {

		Page<RankingDetailResponseDto> searchAllRankings = rankingServiceImpl.searchRankings(keyword, pageable);

		return ResponseEntity.ok(
			ApiResponse
				.<Page<RankingDetailResponseDto>>builder()
				.code(RankingSuccessCode.KEYWORD_PERSONAL_RANKING_SEARCH_SUCCESS.getCode())
				.message(RankingSuccessCode.KEYWORD_PERSONAL_RANKING_SEARCH_SUCCESS.getMessage())
				.status(RankingSuccessCode.KEYWORD_PERSONAL_RANKING_SEARCH_SUCCESS.getStatus())
				.data(searchAllRankings)
				.build()
		);
	}

	@DeleteMapping("/v1/app/individual-rankings/{individualRankingId}")
	@Operation(summary = "랭킹 삭제", description = "랭킹 삭제는 'MASTER' 만 가능'")
	@CheckPermission(roles = {"MASTER"}, mode = CheckPermission.Mode.ALL)
	public ResponseEntity<ApiResponse<DeletePersonalRankingResponseDto>> deletePersonalRanking(
		@PathVariable UUID individualRankingId
	) {

		rankingServiceImpl.deleteRanking(individualRankingId);

		return ResponseEntity.ok(
			ApiResponse
				.<DeletePersonalRankingResponseDto>builder()
				.code(RankingSuccessCode.PERSONAL_RANKING_DELETE_SUCCESS.getCode())
				.message(RankingSuccessCode.PERSONAL_RANKING_DELETE_SUCCESS.getMessage())
				.status(RankingSuccessCode.PERSONAL_RANKING_DELETE_SUCCESS.getStatus())
				.data(null)
				.build()
		);
	}
}
