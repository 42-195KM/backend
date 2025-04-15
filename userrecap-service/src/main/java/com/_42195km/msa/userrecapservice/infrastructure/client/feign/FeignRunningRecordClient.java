package com._42195km.msa.userrecapservice.infrastructure.client.feign;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com._42195km.msa.userrecapservice.application.dto.client.GetRunningRecordAppResponseDto;

@Component
@FeignClient(name = "runningrecord-service", url = "http://localhost:19021")
public interface FeignRunningRecordClient {
	@GetMapping("/api/v1/running-records")
	List<GetRunningRecordAppResponseDto> findAllRunningRecords(
		@RequestParam(name = "startDate") LocalDateTime startDate,
		@RequestParam(name = "endDate") LocalDateTime endDate
	);
}
