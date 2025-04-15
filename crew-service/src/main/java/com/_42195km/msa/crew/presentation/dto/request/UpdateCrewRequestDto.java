package com._42195km.msa.crew.presentation.dto.request;

import com._42195km.msa.crew.application.dto.request.UpdateCrewAppRequestDto;

public record UpdateCrewRequestDto(
	String description,
	Integer capacity,
	Boolean isAutoAgree
) {
	public UpdateCrewAppRequestDto toAppDto() {
		return new UpdateCrewAppRequestDto(
			description,
			capacity,
			isAutoAgree
		);
	}
}
