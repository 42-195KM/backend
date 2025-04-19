package com._42195km.msa.crew.application.dto.response;

import java.util.UUID;

public record ManageNoShowMeetingMemberAppResponseDto(
	UUID crewId,
	UUID meetingId,
	MeetingMemberAppInfo meetingMemberInfo
) {
	public record MeetingMemberAppInfo(
		UUID id,
		UUID userId,
		String status
	) {
	}
}
