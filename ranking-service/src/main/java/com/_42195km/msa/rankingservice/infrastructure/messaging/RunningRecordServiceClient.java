package com._42195km.msa.rankingservice.infrastructure.messaging;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com._42195km.msa.rankingservice.presentation.dto.response.RunningRecordResponseDto;

@FeignClient(name = "runningrecord-service", path = "/api")
public interface RunningRecordServiceClient {

	@GetMapping("/v1/running-records")
	RunningRecordResponseDto getAllRunningRecords(
		@RequestHeader("Authorization") String headerToken,
		@RequestParam int page
	);
}
