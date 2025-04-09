package com._42195km.msa.competitionservice.presentation.controller;

import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._42195km.msa.common.api.ApiResponse;
import com._42195km.msa.competitionservice.application.dto.response.CompetitionAppResponseDto;
import com._42195km.msa.competitionservice.application.exception.CompetitionServiceCode;
import com._42195km.msa.competitionservice.application.mapper.CompetitionMapper;
import com._42195km.msa.competitionservice.application.service.CompetitionService;
import com._42195km.msa.competitionservice.domain.model.Competition;
import com._42195km.msa.competitionservice.presentation.dto.request.CreateCompetitionRequestDto;
import com._42195km.msa.competitionservice.presentation.dto.request.GetCompetitionRequestDto;
import com._42195km.msa.competitionservice.presentation.dto.request.SearchCompetitionRequestDto;
import com._42195km.msa.competitionservice.presentation.dto.response.CompetitionResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/competitions")
@RequiredArgsConstructor
public class CompetitionController {

	private final CompetitionService competitionService;
	private final CompetitionMapper competitionMapper;

	@PostMapping("/")
	@Operation(summary = "대회 생성")
	public ResponseEntity<?> createCompetition(@RequestBody CreateCompetitionRequestDto requestDto) {
		competitionService.createCompetition(requestDto.toCommandDto());
		return ResponseEntity.ok(new ApiResponse<>(
			CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getCode(),
			"대회 생성에 성공했습니다.",
			CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@GetMapping("/")
	@Operation(summary = "대회 전체 조회")
	public ResponseEntity<?> getAllCompetitions(@ModelAttribute @Valid GetCompetitionRequestDto requestDto) {
		Page<CompetitionAppResponseDto> competitions = competitionService.getCompetitions(requestDto.toPageable());
		Page<CompetitionResponseDto> presentationCompetitions = competitionMapper.toPresentationDtoPage(competitions);
		return ResponseEntity.ok(new ApiResponse<>(CompetitionServiceCode.COMPETITION_GET_SUCCESS.getCode(),
			presentationCompetitions,
			CompetitionServiceCode.COMPETITION_GET_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@GetMapping("/search")
	@Operation(summary = "대회 검색")
	public ResponseEntity<?> searchCompetitions(@ParameterObject SearchCompetitionRequestDto requstDto) {
		Page<CompetitionAppResponseDto> competition = competitionService.searchCompetition(requstDto.keyword(),requstDto.toPageable());
		Page<CompetitionResponseDto> presentationCompetition = competitionMapper.toPresentationDtoPage(competition);
		return ResponseEntity.ok(new ApiResponse<>(CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getCode(),
			presentationCompetition,
			CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@GetMapping("/{competitionId}")
	@Operation(summary = "대회 단건 조회")
	public ResponseEntity<?> getCompetition(@PathVariable("competitionId") UUID competitionId) {
		CompetitionAppResponseDto competition = competitionService.getCompetition(competitionId);
		CompetitionResponseDto presentation = competitionMapper.toPresentationDto(competition);
		return ResponseEntity.ok(new ApiResponse<>(CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getCode(),
			presentation,
			CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@GetMapping("/{competitionId}/check")
	@Operation(summary = "주최 대회 확인")
	public ResponseEntity<?> checkCompetition(@PathVariable("competitionId") UUID competitionId) {
		return ResponseEntity.ok(new ApiResponse<>(CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getCode(),
			"",
			CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@PutMapping("/{competitionId}")
	@Operation(summary = "대회 수정")
	public ResponseEntity<?> updateCompetition(@PathVariable("competitionId") UUID competitionId) {
		return ResponseEntity.ok(new ApiResponse<>(CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getCode(),
			"",
			CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));

	}

	@PatchMapping("/{competitionId}")
	@Operation(summary = "대회 삭제")
	public ResponseEntity<?> deleteCompetition(@PathVariable("competitionId") UUID competitionId) {
		return ResponseEntity.ok(new ApiResponse<>(CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getCode(),
			"",
			CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@PostMapping("/{competitionId}/apply")
	@Operation(summary = "대회 신청")
	public ResponseEntity<?> applyCompetition(@PathVariable("competitionId") UUID competitionId,
		@RequestBody Competition competition) {
		return ResponseEntity.ok(new ApiResponse<>(CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getCode(),
			"",
			CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@PostMapping("/payment")
	@Operation(summary = "대회 결제")
	public ResponseEntity<?> payCompetition(@RequestBody Competition competition) {
		return ResponseEntity.ok(new ApiResponse<>(CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getCode(),
			"",
			CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@PostMapping("/draw/{competitionId}")
	@Operation(summary = "대회 추첨")
	public ResponseEntity<?> drawCompetition(@PathVariable("competitionId") UUID competitionId) {
		return ResponseEntity.ok(new ApiResponse<>(CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getCode(),
			"",
			CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}
}
