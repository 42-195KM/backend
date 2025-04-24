package com._42195km.msa.userrecapservice.application.service.client;

import java.time.LocalDateTime;
import java.util.List;

import com._42195km.msa.userrecapservice.application.dto.client.GetRunningRecordAppResponseDto;

public interface RunningRecordClient {
	List<GetRunningRecordAppResponseDto> findAllRunningRecords(
		LocalDateTime startDate, LocalDateTime endDate
	);
}
