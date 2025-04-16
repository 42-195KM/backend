package com._42195km.msa.crew.application.dto.request;

public record UpdateCrewMeetingAppRequestDto(
	String name,
	Integer hour,
	String description,
	Integer capacity
) {
}
