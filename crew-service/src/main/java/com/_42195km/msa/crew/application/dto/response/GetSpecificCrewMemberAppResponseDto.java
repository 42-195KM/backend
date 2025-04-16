package com._42195km.msa.crew.application.dto.response;

import java.util.UUID;

import com._42195km.msa.crew.domain.model.CrewMemberMapping;

public record GetSpecificCrewMemberAppResponseDto(
	UUID id,
	UUID userId,
	String status
) {
	public static GetSpecificCrewMemberAppResponseDto from(CrewMemberMapping memberMapping) {
		return new GetSpecificCrewMemberAppResponseDto(
			memberMapping.getId(),
			memberMapping.getCrewMember().getUserId(),
			memberMapping.getStatus().name()
		);
	}
}
