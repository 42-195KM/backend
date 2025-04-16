package com._42195km.msa.crew.presentation.dto.response;

import java.util.UUID;

import com._42195km.msa.crew.application.dto.response.ExpelCrewMemberAppResponseDto;

public record ExpelCrewMemberResponseDto(
	UUID crewId,
	CrewMemberInfo crewMember
) {
	public record CrewMemberInfo(UUID id, UUID userId, String status) {
	}

	public static ExpelCrewMemberResponseDto from(
		ExpelCrewMemberAppResponseDto dto) {
		return new ExpelCrewMemberResponseDto(
			dto.crewId(),
			new CrewMemberInfo(
				dto.crewMember().id(),
				dto.crewMember().userId(),
				dto.crewMember().status()
			)
		);
	}
}
