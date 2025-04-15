package com._42195km.msa.crew.presentation.controller;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com._42195km.msa.common.api.ApiResponse;
import com._42195km.msa.crew.application.exception.CrewServiceCode;
import com._42195km.msa.crew.application.service.CrewService;
import com._42195km.msa.crew.presentation.dto.request.CreateCrewRequestDto;
import com._42195km.msa.crew.presentation.dto.request.UpdateCrewRequestDto;
import com._42195km.msa.crew.presentation.dto.response.CreateCrewResponseDto;
import com._42195km.msa.crew.presentation.dto.response.GetSpecificCrewResponseDto;
import com._42195km.msa.crew.presentation.dto.response.JoinCrewResponseDto;
import com._42195km.msa.crew.presentation.dto.response.SearchCrewPagingResponseDto;
import com._42195km.msa.crew.presentation.dto.response.UpdateCrewResponseDto;

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

	@GetMapping("/{crewId}")
	public ResponseEntity<ApiResponse<?>> getSpecificCrew(@PathVariable(name = "crewId") UUID crewId) {
		return ResponseEntity.ok(
			new ApiResponse<>(
				CrewServiceCode.CREW_SPECIFIC_GET_SUCCESS.getCode(),
				GetSpecificCrewResponseDto.from(crewService.getSpecificCrew(crewId)),
				CrewServiceCode.CREW_SPECIFIC_GET_SUCCESS.getMessage(),
				CrewServiceCode.CREW_SPECIFIC_GET_SUCCESS.getStatus()
			)
		);
	}

	@GetMapping("/search")
	public ResponseEntity<ApiResponse<?>> searchCrew(
		@RequestParam(name = "keyword", required = false) String keyword,
		@PageableDefault(size = 30, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		return ResponseEntity.ok(
			new ApiResponse<>(
				CrewServiceCode.CREW_SEARCH_GET_SUCCESS.getCode(),
				SearchCrewPagingResponseDto.from(crewService.searchCrew(keyword, pageable)),
				CrewServiceCode.CREW_SEARCH_GET_SUCCESS.getMessage(),
				CrewServiceCode.CREW_SEARCH_GET_SUCCESS.getStatus()
			)
		);
	}

	@PatchMapping("/{crewId}")
	public ResponseEntity<ApiResponse<?>> updateCrew(@PathVariable(name = "crewId") UUID crewId,
		@RequestBody UpdateCrewRequestDto dto) {
		return ResponseEntity.ok(
			new ApiResponse<>(
				CrewServiceCode.CREW_UPDATE_PATCH_SUCCESS.getCode(),
				UpdateCrewResponseDto.from(crewService.updateCrew(crewId, dto.toAppDto())),
				CrewServiceCode.CREW_UPDATE_PATCH_SUCCESS.getMessage(),
				CrewServiceCode.CREW_UPDATE_PATCH_SUCCESS.getStatus()
			)
		);
	}

	@DeleteMapping("/{crewId}")
	public ResponseEntity<ApiResponse<?>> deleteCrew(@PathVariable(name = "crewId") UUID crewId) {
		crewService.deleteCrew(crewId);
		return ResponseEntity.ok(
			new ApiResponse<>(
				CrewServiceCode.CREW_DELETE_DELETE_SUCCESS.getCode(),
				null,
				CrewServiceCode.CREW_DELETE_DELETE_SUCCESS.getMessage(),
				CrewServiceCode.CREW_DELETE_DELETE_SUCCESS.getStatus()
			)
		);
	}

}
