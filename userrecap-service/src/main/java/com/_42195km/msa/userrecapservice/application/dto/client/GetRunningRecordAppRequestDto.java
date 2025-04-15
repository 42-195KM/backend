package com._42195km.msa.userrecapservice.application.dto.client;

import java.time.LocalDateTime;

public record GetRunningRecordAppRequestDto(
	LocalDateTime createdAt
) {
}
