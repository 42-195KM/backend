package com._42195km.msa.crew.application.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import com._42195km.msa.crew.domain.model.CrewMeeting;

public record SearchCrewMeetingAppPagingResponseDto(
	List<GetSpecificCrewMeetingAppResponseDto> content,
	int totalPages,
	long totalElements,
	int currentPage,
	int size,
	boolean first,
	boolean last
) {
	public static SearchCrewMeetingAppPagingResponseDto from(Page<CrewMeeting> crewMeetings) {
		return new SearchCrewMeetingAppPagingResponseDto(
			crewMeetings.getContent().stream()
				.map(crewMeeting -> new GetSpecificCrewMeetingAppResponseDto(
					crewMeeting.getId(),
					crewMeeting.getCrew().getId(),
					crewMeeting.getName(),
					crewMeeting.getMeetingDateTime(),
					crewMeeting.getHour(),
					crewMeeting.getDescription(),
					crewMeeting.getType().name(),
					crewMeeting.getCapacity(),
					crewMeeting.getCrewMeetingMemberMappings().stream().map(
						meetingMember -> new GetSpecificCrewMeetingAppResponseDto.MeetingMemberAppInfo(
							meetingMember.getId(),
							meetingMember.getMeetingMember().getUserId(),
							meetingMember.getStatus().name()
						)).toList()
				)).toList(),
			crewMeetings.getTotalPages(),
			crewMeetings.getTotalElements(),
			crewMeetings.getNumber(),
			crewMeetings.getSize(),
			crewMeetings.isFirst(),
			crewMeetings.isLast()
		);
	}
}
