package com._42195km.msa.crew.application.dto.response;

import java.util.UUID;

import com._42195km.msa.crew.domain.model.CrewMemberMapping;

public record HandleCrewJoinAppResponseDto(
	UUID id,
	UUID crewId,
	CrewMemberAppInfo crewMember
) {
	public static HandleCrewJoinAppResponseDto from(CrewMemberMapping crewMemberMapping) {
		return new HandleCrewJoinAppResponseDto(
			crewMemberMapping.getId(),
			crewMemberMapping.getCrew().getId(),
			new CrewMemberAppInfo(
				crewMemberMapping.getCrewMember().getId(),
				crewMemberMapping.getCrewMember().getUserId(),
				crewMemberMapping.getStatus().name()
			)
		);
	}

	public record CrewMemberAppInfo(
		UUID id,
		UUID userId,
		String status
	) {
	}
}
