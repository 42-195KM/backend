package com._42195km.msa.userrecapservice.infrastructure.client.feign;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import com._42195km.msa.userrecapservice.application.dto.client.GetRunningRecordAppResponseDto;
import com._42195km.msa.userrecapservice.application.service.client.RunningRecordClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FeignRunningRecordClientImpl implements RunningRecordClient {

	private final FeignRunningRecordClient feignRunningRecordClient;

	@Override
	public List<GetRunningRecordAppResponseDto> findAllRunningRecords(
		LocalDateTime startDate, LocalDateTime endDate
	) {
		return feignRunningRecordClient.findAllRunningRecords(startDate, endDate);
	}
}
