package com._42195km.msa.competitionservice.presentation.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com._42195km.msa.common.api.ApiResponse;
import com._42195km.msa.competitionservice.application.service.CompetitionService;
import com._42195km.msa.competitionservice.domain.model.Competition;
import com._42195km.msa.competitionservice.presentation.dto.request.CreateCompetitionRequestDto;
import com._42195km.msa.competitionservice.presentation.exception.CompetitionErrorCode;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/competitions")
@RequiredArgsConstructor
public class CompetitionController {

	private final CompetitionService competitionService;

	@PostMapping("/")
	public ResponseEntity<?> createCompetition(@RequestBody CreateCompetitionRequestDto requestDto) {
		competitionService.createCompetition(requestDto.toCommandDto());
		return ResponseEntity.ok(new ApiResponse<>(
			CompetitionErrorCode.COMPETITION_CREATE_POST_SUCCESS.getCode(),
			"대회 생성에 성공했습니다.",
			CompetitionErrorCode.COMPETITION_CREATE_POST_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@GetMapping("/")
	public ResponseEntity<?> getAllCompetitions() {
		return ResponseEntity.ok(new ApiResponse<>(CompetitionErrorCode.COMPETITION_CREATE_POST_SUCCESS.getCode(),
			"",
			CompetitionErrorCode.COMPETITION_CREATE_POST_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@GetMapping("/search")
	@Operation(description = "")
	public ResponseEntity<?> searchCompetitions(@RequestParam(required = false) String keyword) {
		return ResponseEntity.ok(new ApiResponse<>(CompetitionErrorCode.COMPETITION_CREATE_POST_SUCCESS.getCode(),
			"",
			CompetitionErrorCode.COMPETITION_CREATE_POST_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@GetMapping("/{competitionId}")
	@Operation(description = "")
	public ResponseEntity<?> getCompetition(@PathVariable("competitionId") UUID competitionId) {
		return ResponseEntity.ok(new ApiResponse<>(CompetitionErrorCode.COMPETITION_CREATE_POST_SUCCESS.getCode(),
			"",
			CompetitionErrorCode.COMPETITION_CREATE_POST_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@GetMapping("/{competitionId}/check")
	@Operation(description = "")
	public ResponseEntity<?> checkCompetition(@PathVariable("competitionId") UUID competitionId) {
		return ResponseEntity.ok(new ApiResponse<>(CompetitionErrorCode.COMPETITION_CREATE_POST_SUCCESS.getCode(),
			"",
			CompetitionErrorCode.COMPETITION_CREATE_POST_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@PutMapping("/{competitionId}")
	@Operation(description = "")
	public ResponseEntity<?> updateCompetition(@PathVariable("competitionId") UUID competitionId){
		return ResponseEntity.ok(new ApiResponse<>(CompetitionErrorCode.COMPETITION_CREATE_POST_SUCCESS.getCode(),
			"",
			CompetitionErrorCode.COMPETITION_CREATE_POST_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));

	}

	@PatchMapping("/{competitionId}")
	@Operation(description = "")
	public ResponseEntity<?> deleteCompetition(@PathVariable("competitionId") UUID competitionId){
		return ResponseEntity.ok(new ApiResponse<>(CompetitionErrorCode.COMPETITION_CREATE_POST_SUCCESS.getCode(),
			"",
			CompetitionErrorCode.COMPETITION_CREATE_POST_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@PostMapping("/{competitionId}/apply")
	@Operation(description = "")
	public ResponseEntity<?> applyCompetition(@PathVariable("competitionId") UUID competitionId,@RequestBody Competition competition) {
		return ResponseEntity.ok(new ApiResponse<>(CompetitionErrorCode.COMPETITION_CREATE_POST_SUCCESS.getCode(),
			"",
			CompetitionErrorCode.COMPETITION_CREATE_POST_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@PostMapping("/payment")
	@Operation(description = "")
	public ResponseEntity<?> payCompetition(@RequestBody Competition competition) {
		return ResponseEntity.ok(new ApiResponse<>(CompetitionErrorCode.COMPETITION_CREATE_POST_SUCCESS.getCode(),
			"",
			CompetitionErrorCode.COMPETITION_CREATE_POST_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@PostMapping("/draw/{competitionId}")
	@Operation(description = "")
	public ResponseEntity<?> drawCompetition(@PathVariable("competitionId") UUID competitionId) {
		return ResponseEntity.ok(new ApiResponse<>(CompetitionErrorCode.COMPETITION_CREATE_POST_SUCCESS.getCode(),
			"",
			CompetitionErrorCode.COMPETITION_CREATE_POST_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

}
