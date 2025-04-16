package com._42195km.msa.crew.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record GetSpecificCrewMeetingAppResponseDto(
	UUID id,
	UUID crewId,
	String name,
	LocalDateTime date,
	Integer hour,
	String description,
	String type,
	Integer capacity,
	List<MeetingMemberAppInfo> meetingMembers
) {
	public record MeetingMemberAppInfo(
		UUID id,
		UUID userId,
		String status
	) {
	}
}
