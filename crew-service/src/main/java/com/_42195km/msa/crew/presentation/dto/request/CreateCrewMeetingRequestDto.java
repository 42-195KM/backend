package com._42195km.msa.crew.presentation.dto.request;

import java.time.LocalDateTime;

import com._42195km.msa.crew.application.dto.request.CreateCrewMeetingAppRequestDto;

public record CreateCrewMeetingRequestDto(
	String name,
	LocalDateTime date,
	Integer hour,
	String description,
	String type,
	Integer capacity
) {
	public CreateCrewMeetingAppRequestDto  toAppDto() {
		return new CreateCrewMeetingAppRequestDto(name, date, hour, description, type, capacity);
	}
}
