package com._42195km.msa.crew.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com._42195km.msa.crew.application.dto.response.GetSpecificCrewMeetingAppResponseDto;

public record GetSpecificCrewMeetingResponseDto(
	UUID id,
	UUID crewId,
	String name,
	LocalDateTime date,
	Integer hour,
	String description,
	String type,
	Integer capacity,
	List<MeetingMemberInfo> meetingMembers
) {
	public static GetSpecificCrewMeetingResponseDto from(GetSpecificCrewMeetingAppResponseDto dto) {
		return new GetSpecificCrewMeetingResponseDto(
			dto.id(),
			dto.crewId(),
			dto.name(),
			dto.date(),
			dto.hour(),
			dto.description(),
			dto.type(),
			dto.capacity(),
			dto.meetingMembers().stream().map(
				meetingMember -> new MeetingMemberInfo(
					meetingMember.id(),
					meetingMember.crewMeetingMember().id(),
					meetingMember.crewMeetingMember().status()
				)
			).toList()
		);
	}

	public record MeetingMemberInfo(
		UUID id,
		UUID userId,
		String status
	) {
	}
}
