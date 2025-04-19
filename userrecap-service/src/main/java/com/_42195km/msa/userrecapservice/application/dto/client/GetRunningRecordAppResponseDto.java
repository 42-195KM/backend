package com._42195km.msa.userrecapservice.application.dto.client;

import java.time.Duration;
import java.util.UUID;

import lombok.Builder;

@Builder
public record GetRunningRecordAppResponseDto(
	UUID id,
	UUID userId,
	double distance,
	double pace,
	Duration duration
) {
}
