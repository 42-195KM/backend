package com._42195km.msa.crew.application.dto.request;

import java.time.LocalDateTime;

public record CreateCrewMeetingAppRequestDto(
	String name,
	LocalDateTime date,
	Integer hour,
	String description,
	String type,
	Integer capacity
) {
}
