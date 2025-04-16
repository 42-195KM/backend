package com._42195km.msa.crew.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateCrewMeetingAppResponseDto(
	UUID id,
	UUID crewId,
	String name,
	LocalDateTime date,
	Integer hour,
	String description,
	String type,
	Integer capacity
) {
}
