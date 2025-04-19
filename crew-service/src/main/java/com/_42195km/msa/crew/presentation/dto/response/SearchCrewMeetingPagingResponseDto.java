package com._42195km.msa.crew.presentation.dto.response;

import java.util.List;

import com._42195km.msa.crew.application.dto.response.SearchCrewMeetingAppPagingResponseDto;

public record SearchCrewMeetingPagingResponseDto(
	List<GetSpecificCrewMeetingResponseDto> content,
	int totalPages,
	long totalElements,
	int currentPage,
	int size,
	boolean first,
	boolean last
) {
	public static SearchCrewMeetingPagingResponseDto from(
		SearchCrewMeetingAppPagingResponseDto dto
	) {
		return new SearchCrewMeetingPagingResponseDto(
			dto.content().stream().map(GetSpecificCrewMeetingResponseDto::from).toList(),
			dto.totalPages(),
			dto.totalElements(),
			dto.currentPage(),
			dto.size(),
			dto.first(),
			dto.last()
		);
	}
}
