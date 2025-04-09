package com._42195km.msa.competitionservice.presentation.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com._42195km.msa.common.api.ApiResponse;
import com._42195km.msa.competitionservice.application.exception.CompetitionServiceCode;
import com._42195km.msa.competitionservice.domain.repository.ParticipantRepository;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/competitions/participants")
@RequiredArgsConstructor
public class ParticipantController {

	private final ParticipantRepository participantRepository;

	@GetMapping("")
	@Operation(description = "")
	public ResponseEntity<?> getParticipants() {
		return ResponseEntity.ok(new ApiResponse<>(CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getCode(),
			"",
			CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@GetMapping("/search")
	@Operation(description = "")
	public ResponseEntity<?> getParticipants(@RequestParam(value = "name") String name) {
		return ResponseEntity.ok(new ApiResponse<>(CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getCode(),
			"",
			CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@GetMapping("/{userId}")
	@Operation(description = "")
	public ResponseEntity<?> getParticipant(@PathVariable("userId") UUID userId) {
		return ResponseEntity.ok(new ApiResponse<>(CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getCode(),
			"",
			CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@PutMapping("/cancel/company/{userId}")
	@Operation(description = "")
	public ResponseEntity<?> cancelParticipantByCompany(@PathVariable("userId") UUID userId) {
		return ResponseEntity.ok(new ApiResponse<>(CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getCode(),
			"",
			CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@PutMapping("/cancel/{userId}")
	@Operation(description = "")
	public ResponseEntity<?> cancelParticipant(@PathVariable("userId") UUID userId) {
		return ResponseEntity.ok(new ApiResponse<>(CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getCode(),
			"",
			CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

}
