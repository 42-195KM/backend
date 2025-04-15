package com._42195km.msa.crew.application.dto.request;

public record CreateCrewAppRequestDto(
	String name,
	String description,
	Integer capacity,
	Boolean isAutoAgree
) {
}
