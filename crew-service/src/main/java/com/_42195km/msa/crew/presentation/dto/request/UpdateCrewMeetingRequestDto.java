package com._42195km.msa.crew.presentation.dto.request;

import java.time.LocalDateTime;

import com._42195km.msa.crew.application.dto.request.UpdateCrewMeetingAppRequestDto;

public record UpdateCrewMeetingRequestDto(
	String name,
	Integer hour,
	String description,
	Integer capacity
) {
	public UpdateCrewMeetingAppRequestDto toAppDto() {
		return new UpdateCrewMeetingAppRequestDto(name, hour, description, capacity);
	}
}
