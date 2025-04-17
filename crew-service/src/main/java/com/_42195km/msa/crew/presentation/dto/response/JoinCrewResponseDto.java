package com._42195km.msa.crew.presentation.dto.response;

import java.util.UUID;

import com._42195km.msa.crew.application.dto.response.JoinCrewAppResponseDto;

public record JoinCrewResponseDto(
	UUID crewId,
	UUID crewMemberMappingId,
	CrewMemberInfo crewMember
) {
	public record CrewMemberInfo(
		UUID id,
		UUID userId,
		String status
	) {
	}

	public static JoinCrewResponseDto from(JoinCrewAppResponseDto dto) {
		return new JoinCrewResponseDto(
			dto.crewId(),
			dto.crewMemberMappingId(),
			new CrewMemberInfo(
				dto.crewMember().id(),
				dto.crewMember().userId(),
				dto.crewMember().status()
			)
		);
	}
}
