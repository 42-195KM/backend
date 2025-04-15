package com._42195km.msa.crew.application.dto.request;

public record UpdateCrewAppRequestDto(
	String description,
	Integer capacity,
	Boolean isAutoAgree
) {
}
