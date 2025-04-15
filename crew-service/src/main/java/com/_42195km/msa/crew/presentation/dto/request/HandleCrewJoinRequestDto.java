package com._42195km.msa.crew.presentation.dto.request;

import java.util.UUID;

import com._42195km.msa.crew.application.dto.request.HandleCrewJoinAppRequestDto;

public record HandleCrewJoinRequestDto(
	UUID userId
) {
	public HandleCrewJoinAppRequestDto toAppDto() {
		return new HandleCrewJoinAppRequestDto(
				userId
		);
	}
}
