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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._42195km.msa.common.api.ApiResponse;
import com._42195km.msa.competitionservice.application.dto.CompleteAppDto;
import com._42195km.msa.competitionservice.application.dto.response.CompetitionAppResponseDto;
import com._42195km.msa.competitionservice.application.exception.CompetitionServiceCode;
import com._42195km.msa.competitionservice.application.mapper.CompetitionMapper;
import com._42195km.msa.competitionservice.application.service.CompetitionServiceImpl;
import com._42195km.msa.competitionservice.application.service.SagaServiceImpl;
import com._42195km.msa.competitionservice.infrastructure.messaging.CompetitionSagaOrchestrator;
import com._42195km.msa.competitionservice.presentation.dto.request.CreateCompetitionRequestDto;
import com._42195km.msa.competitionservice.presentation.dto.request.GetRequestDto;
import com._42195km.msa.competitionservice.presentation.dto.request.SearchRequestDto;
import com._42195km.msa.competitionservice.presentation.dto.request.UpdateCompetitionRequestDto;
import com._42195km.msa.competitionservice.presentation.dto.response.CompetitionResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/competitions")
@RequiredArgsConstructor
public class CompetitionController {

	private final CompetitionServiceImpl competitionServiceImpl;
	private final SagaServiceImpl sagaServiceImpl;

	private final CompetitionMapper competitionMapper;
	private final CompetitionSagaOrchestrator sagaOrchestrator;

	@PostMapping("/")
	@Operation(summary = "대회 생성")
	public ResponseEntity<?> createCompetition(@RequestBody CreateCompetitionRequestDto requestDto) {
		competitionServiceImpl.createCompetition(requestDto.toCommandDto());
		return ResponseEntity.ok(new ApiResponse<>(
			CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getCode(),
			"대회 생성에 성공했습니다.",
			CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@GetMapping("/")
	@Operation(summary = "대회 전체 조회")
	public ResponseEntity<?> getAllCompetitions(@ModelAttribute @Valid GetRequestDto requestDto) {
		Page<CompetitionAppResponseDto> competitions = competitionServiceImpl.getCompetitions(requestDto.toPageable());
		Page<CompetitionResponseDto> presentationCompetitions = competitionMapper.toPresentationDtoPage(competitions);
		return ResponseEntity.ok(new ApiResponse<>(CompetitionServiceCode.COMPETITION_GET_SUCCESS.getCode(),
			presentationCompetitions,
			CompetitionServiceCode.COMPETITION_GET_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@GetMapping("/search")
	@Operation(summary = "대회 검색")
	public ResponseEntity<?> searchCompetitions(@ParameterObject SearchRequestDto requstDto) {
		Page<CompetitionAppResponseDto> competition = competitionServiceImpl.searchCompetition(requstDto.keyword(),
			requstDto.toPageable());
		Page<CompetitionResponseDto> presentationCompetition = competitionMapper.toPresentationDtoPage(competition);
		return ResponseEntity.ok(new ApiResponse<>(CompetitionServiceCode.COMPETITION_SEARCH_SUCCESS.getCode(),
			presentationCompetition,
			CompetitionServiceCode.COMPETITION_SEARCH_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@GetMapping("/{competitionId}")
	@Operation(summary = "대회 단건 조회")
	public ResponseEntity<?> getCompetition(@PathVariable("competitionId") UUID competitionId) {
		CompetitionAppResponseDto competition = competitionServiceImpl.getCompetition(competitionId);
		CompetitionResponseDto presentation = competitionMapper.toPresentationDto(competition);
		return ResponseEntity.ok(new ApiResponse<>(CompetitionServiceCode.COMPETITION_GET_SUCCESS.getCode(),
			presentation,
			CompetitionServiceCode.COMPETITION_GET_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@GetMapping("/{competitionId}/check")
	@Operation(summary = "주최 대회 확인")
	public ResponseEntity<?> checkCompetition(@PathVariable("competitionId") UUID userId,
		@ParameterObject GetRequestDto requestDto) {
		Page<CompetitionAppResponseDto> competition = competitionServiceImpl.getHostCompetition(userId,
			requestDto.toPageable());
		Page<CompetitionResponseDto> presentationCompetition = competitionMapper.toPresentationDtoPage(competition);
		return ResponseEntity.ok(new ApiResponse<>(CompetitionServiceCode.COMPETITION_GET_SUCCESS.getCode(),
			presentationCompetition,
			CompetitionServiceCode.COMPETITION_GET_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@PatchMapping("/{competitionId}")
	@Operation(summary = "대회 수정")
	public ResponseEntity<?> updateCompetition(@PathVariable("competitionId") UUID competitionId,
		@RequestBody UpdateCompetitionRequestDto requestDto) {
		competitionServiceImpl.updateCompetition(competitionId, requestDto.toCommandDto());
		return ResponseEntity.ok(new ApiResponse<>(CompetitionServiceCode.COMPETITION_UPDATE_SUCCESS.getCode(),
			"대회 수정이 완료되었습니다.",
			CompetitionServiceCode.COMPETITION_UPDATE_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));

	}

	@PatchMapping("/{competitionId}/delete")
	@Operation(summary = "대회 삭제")
	public ResponseEntity<?> deleteCompetition(@PathVariable("competitionId") UUID competitionId) {
		competitionServiceImpl.deleteCompetition(competitionId);
		return ResponseEntity.ok(new ApiResponse<>(CompetitionServiceCode.COMPETITION_DELETE_SUCCESS.getCode(),
			"대회 삭제가 왼료되었습니다.",
			CompetitionServiceCode.COMPETITION_DELETE_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@PostMapping("/draw/{competitionId}")
	@Operation(summary = "대회 추첨")
	public ResponseEntity<?> drawCompetition(@PathVariable("competitionId") UUID competitionId) {
		competitionServiceImpl.drawCompetition(competitionId);
		return ResponseEntity.ok(new ApiResponse<>(CompetitionServiceCode.COMPETITION_DRAW_SUCCESS.getCode(),
			"대회 추첨이 완료되었습니다.",
			CompetitionServiceCode.COMPETITION_DRAW_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@PostMapping("/complete")
	@Operation(summary = "대회 신청 프로세스 - 분산 트랜젝션 도입 - 모든 단계 통합")
	public ResponseEntity<?> completeApplication(@RequestBody CompleteAppDto requestDto) {

		String response = sagaServiceImpl.processCompleteApplication(requestDto);

		return ResponseEntity.ok(new ApiResponse<>(
			CompetitionServiceCode.COMPETITION_APPLY_SUCCESS.getCode(),
			response,
			CompetitionServiceCode.COMPETITION_APPLY_SUCCESS.getMessage(),
			HttpStatus.OK.value()
		));
	}

	@GetMapping("/{competitionId}/{participantId}/status")
	@Operation(summary = "대회 신청 상태 조회")
	public ResponseEntity<?> getApplicationStatus(
		@PathVariable("competitionId") UUID competitionId,
		@PathVariable("participantId") UUID participantId) {
		String result = sagaServiceImpl.findActiveSagaId(competitionId, participantId);
		return ResponseEntity.ok(new ApiResponse<>(
			CompetitionServiceCode.COMPETITION_GET_SUCCESS.getCode(),
			result,
			CompetitionServiceCode.COMPETITION_GET_SUCCESS.getMessage(),
			HttpStatus.OK.value()
		));
	}

}
