package com._42195km.msa.crew.presentation.dto.response;

import java.util.UUID;

import com._42195km.msa.crew.application.dto.response.GetSpecificCrewMemberAppResponseDto;

public record GetSpecificCrewMemberResponseDto(
	UUID id,
	UUID userId,
	String status
) {
	public static GetSpecificCrewMemberResponseDto from(GetSpecificCrewMemberAppResponseDto dto) {
		return new GetSpecificCrewMemberResponseDto(
			dto.id(),
			dto.userId(),
			dto.status()
		);
	}
}
