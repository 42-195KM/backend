package com._42195km.msa.crew.presentation.dto.response;

import java.util.UUID;

import com._42195km.msa.crew.application.dto.response.ManageNoShowMeetingMemberAppResponseDto;

public record ManageNoShowMeetingMemberResponseDto(
	UUID crewId,
	UUID meetingId,
	MeetingMemberInfo meetingMemberInfo
) {
	public record MeetingMemberInfo(
		UUID id,
		UUID userId,
		String status
	) {
	}

	public static ManageNoShowMeetingMemberResponseDto from(
		ManageNoShowMeetingMemberAppResponseDto dto) {
		return new ManageNoShowMeetingMemberResponseDto(
			dto.crewId(),
			dto.meetingId(),
			new MeetingMemberInfo(
				dto.meetingMemberInfo().id(),
				dto.meetingMemberInfo().userId(),
				dto.meetingMemberInfo().status()
			)
		);
	}
}
