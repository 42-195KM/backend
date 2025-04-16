package com._42195km.msa.crew.presentation.dto.response;

import java.util.UUID;

import com._42195km.msa.crew.application.dto.response.ParticipateCrewMeetingAppResponseDto;

public record ParticipateCrewMeetingResponseDto(
	UUID crewId,
	UUID crewMeetingMemberMappingId,
	CrewMeetingMemberInfo crewMeetingMember
) {
	public record CrewMeetingMemberInfo(
		UUID id,
		UUID userId,
		String status
	) {
	}

	public static ParticipateCrewMeetingResponseDto from(
		ParticipateCrewMeetingAppResponseDto dto) {
		return new ParticipateCrewMeetingResponseDto(
			dto.crewId(),
			dto.crewMeetingMemberMappingId(),
			new CrewMeetingMemberInfo(
				dto.crewMeetingMember().id(),
				dto.crewMeetingMember().userId(),
				dto.crewMeetingMember().status()
			)
		);
	}
}
