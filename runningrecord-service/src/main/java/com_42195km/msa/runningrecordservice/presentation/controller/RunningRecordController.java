package com_42195km.msa.runningrecordservice.presentation.controller;

import org.springframework.http.ResponseEntity;
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

		ApiResponse<Object> response = ApiResponse.builder()
			.code(runningRecordCreateSuccess.getCode())
			.message(runningRecordCreateSuccess.getMessage())
			.status(runningRecordCreateSuccess.getStatus())
			.data(createRunningRecordResponseDto)
			.build();

		return ResponseEntity.ok(response);
	}
}
