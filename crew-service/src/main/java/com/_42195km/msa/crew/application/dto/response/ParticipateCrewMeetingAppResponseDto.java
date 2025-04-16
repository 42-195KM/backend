package com._42195km.msa.crew.application.dto.response;

import java.util.UUID;

public record ParticipateCrewMeetingAppResponseDto(
	UUID crewId,
	UUID crewMeetingMemberMappingId,
	CrewMeetingMemberAppInfo crewMeetingMember
) {
	public record CrewMeetingMemberAppInfo(
		UUID id,
		UUID userId,
		String status
	) {
	}
}
