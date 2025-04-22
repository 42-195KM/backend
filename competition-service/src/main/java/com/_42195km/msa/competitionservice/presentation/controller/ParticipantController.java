package com._42195km.msa.competitionservice.presentation.controller;

import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com._42195km.msa.common.api.ApiResponse;
import com._42195km.msa.competitionservice.application.dto.response.ParticipantAppResponseDto;
import com._42195km.msa.competitionservice.application.dto.response.SearchParticipantAppResponseDto;
import com._42195km.msa.competitionservice.application.exception.CompetitionServiceCode;
import com._42195km.msa.competitionservice.application.mapper.ParticipantMapper;
import com._42195km.msa.competitionservice.application.service.ParticipantServiceImpl;
import com._42195km.msa.competitionservice.presentation.dto.request.CancelParticipantRequestDto;
import com._42195km.msa.competitionservice.presentation.dto.request.GetRequestDto;
import com._42195km.msa.competitionservice.presentation.dto.request.SearchRequestDto;
import com._42195km.msa.competitionservice.presentation.dto.response.SearchResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/competitions/participants")
@RequiredArgsConstructor
public class ParticipantController {

	private final ParticipantServiceImpl participantService;
	private final ParticipantMapper participantMapper;

	@GetMapping("/{competitionId}")
	@Operation(summary = "대회 참가자 조회")
	public ResponseEntity<?> getParticipants(@ModelAttribute @Valid GetRequestDto requestDto,
		@RequestParam("competitionId") UUID competitionId) {
		Page<ParticipantAppResponseDto> participants = participantService.getParticipants(requestDto.toPageable(),
			competitionId);
		return ResponseEntity.ok(new ApiResponse<>(CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getCode(),
			participants,
			CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@GetMapping("/search")
	@Operation(summary = "참가자 검색")
	public ResponseEntity<?> getParticipants(@ParameterObject @Valid SearchRequestDto requestDto) {
		Page<SearchParticipantAppResponseDto> participants = participantService.searchParticipants(requestDto.keyword(),
			requestDto.searchType(), requestDto.toPageable());
		Page<SearchResponseDto> presentationParticipants = participantMapper.toPresentationDtoPage(participants);
		return ResponseEntity.ok(new ApiResponse<>(CompetitionServiceCode.PARTICIPANT_SEARCH_SUCCESS.getCode(),
			presentationParticipants,
			CompetitionServiceCode.PARTICIPANT_SEARCH_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@GetMapping("/show/{userId}")
	@Operation(summary = "참가자 id로 조회")
	public ResponseEntity<?> getParticipant(@ParameterObject SearchRequestDto requestDto) {
		Page<SearchParticipantAppResponseDto> participant = participantService.getParticipant(requestDto.keyword(),
			requestDto.toPageable());
		Page<SearchResponseDto> presentationParticipant = participantMapper.toPresentationDtoPage(participant);
		return ResponseEntity.ok(new ApiResponse<>(CompetitionServiceCode.PARTICIPANT_GET_SUCCESS.getCode(),
			presentationParticipant,
			CompetitionServiceCode.PARTICIPANT_GET_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@PutMapping("/cancel/company")
	@Operation(summary = "주최측의 신청 취소")
	public ResponseEntity<?> cancelParticipantByCompany(@ParameterObject CancelParticipantRequestDto requestDto) {
		participantService.cancelParticipantByCompany(requestDto);
		return ResponseEntity.ok(new ApiResponse<>(CompetitionServiceCode.PARTICIPANT_CANCEL_SUCCESS.getCode(),
			"신청 취소가 완료되었습니다.",
			CompetitionServiceCode.PARTICIPANT_CANCEL_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@PutMapping("/cancel")
	@Operation(summary = "신청 취소")
	public ResponseEntity<?> cancelParticipant(@ParameterObject CancelParticipantRequestDto requestDto) {
		participantService.cancelParticipant(requestDto);
		return ResponseEntity.ok(new ApiResponse<>(CompetitionServiceCode.PARTICIPANT_CANCEL_SUCCESS.getCode(),
			"신청 취소가 완료되었습니다.",
			CompetitionServiceCode.PARTICIPANT_CANCEL_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

}
