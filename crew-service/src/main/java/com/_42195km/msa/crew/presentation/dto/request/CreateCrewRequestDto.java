package com._42195km.msa.crew.presentation.dto.request;

import com._42195km.msa.crew.application.dto.request.CreateCrewAppRequestDto;

public record CreateCrewRequestDto(
	String name,
	String description,
	Integer capacity,
	Boolean isAutoAgree
) {
	public CreateCrewAppRequestDto toAppDto() {
		return new CreateCrewAppRequestDto(
			name,
			description,
			capacity,
			isAutoAgree
		);
	}
}
