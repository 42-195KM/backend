package com._42195km.msa.crew.presentation.dto.response;

import java.util.UUID;

import com._42195km.msa.crew.application.dto.response.HandleCrewJoinAppResponseDto;

public record HandleCrewJoinResponseDto(
	UUID id,
	UUID crewId,
	UUID crewMemberMappingId,
	CrewMemberInfo crewMember,
) {
	public record CrewMemberInfo(
		UUID id,
		UUID userId,
		String status
	) {
	}

	public static HandleCrewJoinResponseDto from(HandleCrewJoinAppResponseDto dto) {
		return new HandleCrewJoinResponseDto(
				dto.id(),
				dto.crewId(),
				dto.crewMemberMappingId(),
				new CrewMemberInfo(
						dto.crewMember().id()
						dto.crewMember().userId(),
						dto.crewMember().status()
				),
		);
	}
}
