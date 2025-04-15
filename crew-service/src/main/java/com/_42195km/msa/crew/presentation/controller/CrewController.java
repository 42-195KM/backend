package com._42195km.msa.crew.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._42195km.msa.common.api.ApiResponse;
import com._42195km.msa.crew.application.exception.CrewServiceCode;
import com._42195km.msa.crew.application.service.CrewService;
import com._42195km.msa.crew.presentation.dto.request.CreateCrewRequestDto;
import com._42195km.msa.crew.presentation.dto.response.CreateCrewResponseDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/crews")
@RequiredArgsConstructor
public class CrewController {
	private final CrewService crewService;

	@PostMapping
	public ResponseEntity<ApiResponse<?>> createCrew(@RequestBody CreateCrewRequestDto dto) {
		return ResponseEntity.ok(
			new ApiResponse<>(
				CrewServiceCode.CREW_CREATE_POST_SUCCESS.getCode(),
				CreateCrewResponseDto.from(crewService.createCrew(dto.toAppDto())),
				CrewServiceCode.CREW_CREATE_POST_SUCCESS.getMessage(),
				CrewServiceCode.CREW_CREATE_POST_SUCCESS.getStatus()
			)
		);
	}

	@PostMapping("/{crewId}/join")
	public ResponseEntity<ApiResponse<?>> applyJoiningCrew(@PathVariable(name = "crewId") UUID crewId) {
		return ResponseEntity.ok(
			new ApiResponse<>(
				CrewServiceCode.CREW_APPLY_JOIN_POST_SUCCESS.getCode(),
				JoinCrewResponseDto.from(crewService.applyJoiningCrew(crewId, null)),
				CrewServiceCode.CREW_APPLY_JOIN_POST_SUCCESS.getMessage(),
				CrewServiceCode.CREW_APPLY_JOIN_POST_SUCCESS.getStatus()
			)
		);

}
