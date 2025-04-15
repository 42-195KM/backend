package com._42195km.msa.crew.application.dto.response;

import java.util.UUID;

public record JoinCrewAppResponseDto(
	UUID id,
	UUID crewId,
	UUID crewMemberMappingId,
	CrewMemberAppInfo crewMember
) {
	public record CrewMemberAppInfo(
		UUID id,
		UUID userId,
		String status
	) {
	}
}
