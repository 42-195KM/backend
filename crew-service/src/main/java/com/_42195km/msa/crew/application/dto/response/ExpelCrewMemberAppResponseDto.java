package com._42195km.msa.crew.application.dto.response;

import java.util.UUID;

import com._42195km.msa.crew.domain.model.CrewMemberMapping;

public record ExpelCrewMemberAppResponseDto(
	UUID crewId,
	CrewMemberAppInfo crewMember
) {
	public record CrewMemberAppInfo(UUID id, UUID userId, String status) {
	}

	public static ExpelCrewMemberAppResponseDto from(CrewMemberMapping crewMemberMapping) {
		return new ExpelCrewMemberAppResponseDto(
			crewMemberMapping.getCrew().getId(),
			new CrewMemberAppInfo(
				crewMemberMapping.getId(),
				crewMemberMapping.getCrewMember().getUserId(),
				crewMemberMapping.getStatus().name()
			)
		);
	}
}
