package com_42195km.msa.runningrecordservice.presentation.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com._42195km.msa.common.api.ApiResponse;

import com_42195km.msa.runningrecordservice.application.dto.request.CreateRunningRecordCommandDto;
import com_42195km.msa.runningrecordservice.application.service.RunningRecordService;
import com_42195km.msa.runningrecordservice.domain.model.RunningRecord;
import com_42195km.msa.runningrecordservice.infrastructure.config.RunningRecordServiceCode;
import com_42195km.msa.runningrecordservice.presentation.dto.request.CreateRunningRecordRequestDto;
import com_42195km.msa.runningrecordservice.presentation.dto.response.CreateRunningRecordResponseDto;
import com_42195km.msa.runningrecordservice.presentation.dto.response.DeleteRunningRecordResponseDto;
import com_42195km.msa.runningrecordservice.presentation.dto.response.GetRunningRecordResponseDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/running-records")
@RequiredArgsConstructor
public class RunningRecordController {
	private final RunningRecordService runningRecordService;

	// 러닝 기록 생성 (POST api/v1/running-records)
	@PostMapping
	public ResponseEntity<?> createRunningRecord(@RequestBody CreateRunningRecordRequestDto dto) {
		CreateRunningRecordCommandDto createRunningRecordCommandDto = dto.toCommandDto();
		RunningRecord runningRecord = runningRecordService.createRunningRecord(createRunningRecordCommandDto);
		CreateRunningRecordResponseDto responseDto = new CreateRunningRecordResponseDto(runningRecord);

		RunningRecordServiceCode runningRecordCreateSuccess = RunningRecordServiceCode.RUNNING_RECORD_CREATE_SUCCESS;

		ApiResponse<CreateRunningRecordResponseDto> response = ApiResponse.<CreateRunningRecordResponseDto>builder()
			.code(runningRecordCreateSuccess.getCode())
			.message(runningRecordCreateSuccess.getMessage())
			.status(runningRecordCreateSuccess.getStatus())
			.data(responseDto)
			.build();

		return ResponseEntity.ok(response);
	}

	// 러닝 기록 조회 (GET api/v1/running-records/{runningRecordId})
	@GetMapping("/{runningRecordId}")
	public ResponseEntity<?> getRunningRecord(@PathVariable UUID runningRecordId) {
		RunningRecord runningRecord = runningRecordService.getRecordById(runningRecordId);
		GetRunningRecordResponseDto responseDto = new GetRunningRecordResponseDto(runningRecord);

		RunningRecordServiceCode runningRecordGetSuccess = RunningRecordServiceCode.RUNNING_RECORD_GET_SUCCESS;

		ApiResponse<GetRunningRecordResponseDto> response = ApiResponse.<GetRunningRecordResponseDto>builder()
			.code(runningRecordGetSuccess.getCode())
			.message(runningRecordGetSuccess.getMessage())
			.status(runningRecordGetSuccess.getStatus())
			.data(responseDto)
			.build();

		return ResponseEntity.ok(response);
	}

	// 러닝 기록 목록 (GET api/v1/running-records)
	@GetMapping
	public ResponseEntity<?> getAllRunningRecords(
		@RequestParam(defaultValue = "0", required = false) int page,
		@RequestParam(defaultValue = "10", required = false) int size)
	{
		PageRequest pageRequest = PageRequest.of(page, size);
		Page<GetRunningRecordResponseDto> responseDtos = runningRecordService.getAllRecords(pageRequest)
			.map(GetRunningRecordResponseDto::new);

		RunningRecordServiceCode runningRecordGetAllSuccess = RunningRecordServiceCode.RUNNING_RECORD_GET_ALL_SUCCESS;

		ApiResponse<Page<GetRunningRecordResponseDto>> response = ApiResponse.<Page<GetRunningRecordResponseDto>>builder()
			.code(runningRecordGetAllSuccess.getCode())
			.message(runningRecordGetAllSuccess.getMessage())
			.status(runningRecordGetAllSuccess.getStatus())
			.data(responseDtos)
			.build();

		return ResponseEntity.ok(response);
	}

	// 러닝 기록 검색 (GET api/v1/running-records/search)
	@GetMapping("/search")
	public ResponseEntity<?> searchRecords(
		@RequestParam(name = "userId", required = true) UUID userId,
		@RequestParam(defaultValue = "0", required = false) int page,
		@RequestParam(defaultValue = "10", required = false) int size
	) {
		PageRequest pageRequest = PageRequest.of(page, size);
		Page<GetRunningRecordResponseDto> responseDtos = runningRecordService.searchRecords(userId, pageRequest)
			.map(GetRunningRecordResponseDto::new);

		RunningRecordServiceCode runningRecordSearchSuccess = RunningRecordServiceCode.RUNNING_RECORD_SEARCH_SUCCESS;

		ApiResponse<Page<GetRunningRecordResponseDto>> response = ApiResponse.<Page<GetRunningRecordResponseDto>>builder()
			.code(runningRecordSearchSuccess.getCode())
			.message(runningRecordSearchSuccess.getMessage())
			.status(runningRecordSearchSuccess.getStatus())
			.data(responseDtos)
			.build();

		return ResponseEntity.ok(response);
	}

	// 러닝 기록 삭제 (DELETE api/v1/running-records/{runningRecordId})
	@DeleteMapping("/{runningRecordId}")
	public ResponseEntity<?> deleteRunningRecord(@PathVariable UUID runningRecordId) {
		RunningRecord runningRecord = runningRecordService.deleteRecord(runningRecordId);
		DeleteRunningRecordResponseDto responseDto = new DeleteRunningRecordResponseDto(runningRecord);

		RunningRecordServiceCode runningRecordDeleteSuccess = RunningRecordServiceCode.RUNNING_RECORD_DELETE_SUCCESS;

		ApiResponse<DeleteRunningRecordResponseDto> response = ApiResponse.<DeleteRunningRecordResponseDto>builder()
			.code(runningRecordDeleteSuccess.getCode())
			.message(runningRecordDeleteSuccess.getMessage())
			.status(runningRecordDeleteSuccess.getStatus())
			.data(responseDto)
			.build();

		return ResponseEntity.ok(response);
	}
}
