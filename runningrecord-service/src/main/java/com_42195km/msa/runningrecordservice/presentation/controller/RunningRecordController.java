package com_42195km.msa.runningrecordservice.presentation.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._42195km.msa.common.api.ApiResponse;

import com_42195km.msa.runningrecordservice.application.dto.request.CreateRunningRecordCommandDto;
import com_42195km.msa.runningrecordservice.application.service.RunningRecordService;
import com_42195km.msa.runningrecordservice.domain.model.RunningRecord;
import com_42195km.msa.runningrecordservice.infrastructure.config.RunningRecordServiceCode;
import com_42195km.msa.runningrecordservice.presentation.dto.request.CreateRunningRecordRequestDto;
import com_42195km.msa.runningrecordservice.presentation.dto.response.CreateRunningRecordResponseDto;
import com_42195km.msa.runningrecordservice.presentation.dto.response.GetRunningRecordResponseDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/running-records")
@RequiredArgsConstructor
public class RunningRecordController {
	private final RunningRecordService runningRecordService;

	// 러닝 기록 생성 (POST api/v1/running-records)
	@PostMapping("")
	public ResponseEntity<?> createRunningRecord(@RequestBody CreateRunningRecordRequestDto dto) {
		CreateRunningRecordCommandDto createRunningRecordCommandDto = dto.toCommandDto();
		RunningRecord runningRecord = runningRecordService.createRunningRecord(createRunningRecordCommandDto);
		CreateRunningRecordResponseDto createRunningRecordResponseDto = new CreateRunningRecordResponseDto(runningRecord);

		RunningRecordServiceCode runningRecordCreateSuccess = RunningRecordServiceCode.RUNNING_RECORD_CREATE_SUCCESS;

		ApiResponse<CreateRunningRecordResponseDto> response = ApiResponse.<CreateRunningRecordResponseDto>builder()
			.code(runningRecordCreateSuccess.getCode())
			.message(runningRecordCreateSuccess.getMessage())
			.status(runningRecordCreateSuccess.getStatus())
			.data(createRunningRecordResponseDto)
			.build();

		return ResponseEntity.ok(response);
	}

	// 러닝 기록 조회 (GET api/v1/running-records/{recordId})
	@GetMapping("/{recordId}")
	public ResponseEntity<?> getRunningRecord(@PathVariable UUID runningRecordId) {
		RunningRecord runningRecord = runningRecordService.getRecordById(runningRecordId);
		GetRunningRecordResponseDto getRunningRecordRequestDto = new GetRunningRecordResponseDto(runningRecord);

		RunningRecordServiceCode runningRecordGetSuccess = RunningRecordServiceCode.RUNNING_RECORD_GET_SUCCESS;

		ApiResponse<GetRunningRecordResponseDto> response = ApiResponse.<GetRunningRecordResponseDto>builder()
			.code(runningRecordGetSuccess.getCode())
			.message(runningRecordGetSuccess.getMessage())
			.status(runningRecordGetSuccess.getStatus())
			.data(getRunningRecordRequestDto)
			.build();

		return ResponseEntity.ok(response);
	}
}
