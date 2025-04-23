package com._42195km.msa.runningrecordservice.presentation.controller;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com._42195km.msa.common.aop.CheckPermission;

import com._42195km.msa.common.controller.BaseController;
import com._42195km.msa.runningrecordservice.application.dto.request.CreateRunningRecordCommandDto;
import com._42195km.msa.runningrecordservice.application.service.RunningRecordService;
import com._42195km.msa.runningrecordservice.domain.model.RunningRecord;
import com._42195km.msa.runningrecordservice.infrastructure.code.RunningRecordServiceCode;
import com._42195km.msa.runningrecordservice.presentation.dto.request.CreateRunningRecordRequestDto;
import com._42195km.msa.runningrecordservice.presentation.dto.response.CreateRunningRecordResponseDto;
import com._42195km.msa.runningrecordservice.presentation.dto.response.DeleteRunningRecordResponseDto;
import com._42195km.msa.runningrecordservice.presentation.dto.response.GetRunningRecordResponseDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RunningRecordController extends BaseController {
	private final RunningRecordService runningRecordService;

	// 러닝 기록 생성 (POST api/v1/running-records)
	@PostMapping("/running-records")
	public ResponseEntity<?> createRunningRecord(@RequestBody CreateRunningRecordRequestDto dto) {
		CreateRunningRecordCommandDto createRunningRecordCommandDto = dto.toCommandDto();
		RunningRecord runningRecord = runningRecordService.createRunningRecord(createRunningRecordCommandDto);
		CreateRunningRecordResponseDto responseDto = new CreateRunningRecordResponseDto(runningRecord);

		return createOkResponseEntity(responseDto, RunningRecordServiceCode.RUNNING_RECORD_CREATE_SUCCESS);
	}

	// 러닝 기록 조회 (GET api/v1/running-records/{runningRecordId})
	@GetMapping("/running-records/{runningRecordId}")
	public ResponseEntity<?> getRunningRecord(@PathVariable UUID runningRecordId) {
		RunningRecord runningRecord = runningRecordService.getRecordById(runningRecordId);
		GetRunningRecordResponseDto responseDto = new GetRunningRecordResponseDto(runningRecord);

		return createOkResponseEntity(responseDto, RunningRecordServiceCode.RUNNING_RECORD_GET_SUCCESS);
	}

	// 러닝 기록 목록 (GET api/v1/running-records)
	@GetMapping("/running-records")
	public ResponseEntity<?> getAllRunningRecords(
		@RequestParam(defaultValue = "0", required = false) int page,
		@RequestParam(defaultValue = "10", required = false) int size)
	{
		PageRequest pageRequest = PageRequest.of(page, size);
		Page<GetRunningRecordResponseDto> responseDtos = runningRecordService.getAllRecords(pageRequest)
			.map(GetRunningRecordResponseDto::new);

		return createOkResponseEntity(responseDtos, RunningRecordServiceCode.RUNNING_RECORD_GET_ALL_SUCCESS);
	}

	// 러닝 기록 검색 (GET api/v1/running-records/search)
	@GetMapping("/running-records/search")
	public ResponseEntity<?> searchRecords(
		@RequestParam(name = "userId", required = true) UUID userId,
		@RequestParam(name = "createdAt", required = false)
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdAt,
		@RequestParam(defaultValue = "0", required = false) int page,
		@RequestParam(defaultValue = "10", required = false) int size
	) {
		PageRequest pageRequest = PageRequest.of(page, size);

		// createdAt이 전달되지 않은 경우: 전체 기간 검색을 위해 매우 이전 시점을 기본값으로 사용
		LocalDateTime searchStart = (createdAt != null) ? createdAt :
			LocalDateTime.of(1970, 1, 1, 0, 0);

		Page<GetRunningRecordResponseDto> responseDtos = runningRecordService.searchRecords(userId, searchStart, pageRequest)
			.map(GetRunningRecordResponseDto::new);

		return createOkResponseEntity(responseDtos, RunningRecordServiceCode.RUNNING_RECORD_SEARCH_SUCCESS);
	}

	// 러닝 기록 삭제 (DELETE api/v1/app/running-records/{runningRecordId})
	@DeleteMapping("app/running-records/{runningRecordId}")
	@CheckPermission(roles = {"MASTER"}, mode = CheckPermission.Mode.ALL)
	public ResponseEntity<?> deleteRunningRecord(@PathVariable UUID runningRecordId) {
		RunningRecord runningRecord = runningRecordService.deleteRecord(runningRecordId);
		DeleteRunningRecordResponseDto responseDto = new DeleteRunningRecordResponseDto(runningRecord);
		return createOkResponseEntity(responseDto, RunningRecordServiceCode.RUNNING_RECORD_DELETE_SUCCESS);
	}
}
