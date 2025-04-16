package com._42195km.msa.crew.presentation.dto.response;

import java.util.UUID;

import com._42195km.msa.crew.application.dto.response.UpdateCrewAppResponseDto;

public record UpdateCrewResponseDto(
	UUID id,
	String name,
	String description,
	UUID captainId,
	Integer capacity,
	Boolean isAutoAgree
) {
	public static UpdateCrewResponseDto from(UpdateCrewAppResponseDto dto) {
		return new UpdateCrewResponseDto(
			dto.id(),
			dto.name(),
			dto.description(),
			dto.captainId(),
			dto.capacity(),
			dto.isAutoAgree()
		);
	}
}
