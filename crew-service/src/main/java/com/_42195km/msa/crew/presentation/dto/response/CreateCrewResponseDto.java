package com._42195km.msa.crew.presentation.dto.response;

import java.util.UUID;

import com._42195km.msa.crew.application.dto.response.CreateCrewAppResponseDto;

public record CreateCrewResponseDto(
	UUID id,
	String name,
	String description,
	UUID captainId,
	Integer capacity,
	Boolean isAutoAgree
) {
	public static CreateCrewResponseDto from(CreateCrewAppResponseDto dto) {
		return new CreateCrewResponseDto(
			dto.id(),
			dto.name(),
			dto.description(),
			dto.captainId(),
			dto.capacity(),
			dto.isAutoAgree()
		);
	}
}
