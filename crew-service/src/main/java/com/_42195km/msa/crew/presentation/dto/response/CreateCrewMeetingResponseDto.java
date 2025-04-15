package com._42195km.msa.crew.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com._42195km.msa.crew.application.dto.response.CreateCrewMeetingAppResponseDto;

public record CreateCrewMeetingResponseDto(
	UUID id,
	UUID crewId,
	String name,
	LocalDateTime date,
	Integer hour,
	String description,
	String type,
	Integer capacity
) {
	public static CreateCrewMeetingResponseDto from(CreateCrewMeetingAppResponseDto dto) {
		return new CreateCrewMeetingResponseDto(
			dto.id(),
			dto.crewId(),
			dto.name(),
			dto.date(),
			dto.hour(),
			dto.description(),
			dto.type(),
			dto.capacity()
		);
	}
}
