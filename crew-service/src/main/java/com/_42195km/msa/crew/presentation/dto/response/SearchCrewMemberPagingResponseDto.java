package com._42195km.msa.crew.presentation.dto.response;

import java.util.List;

import com._42195km.msa.crew.application.dto.response.SearchCrewMemberAppPagingResponseDto;

public record SearchCrewMemberPagingResponseDto(
	List<GetSpecificCrewMemberResponseDto> content,
	int totalPages,
	long totalElements,
	int currentPage,
	int size,
	boolean first,
	boolean last
) {
	public static SearchCrewMemberPagingResponseDto from(
		SearchCrewMemberAppPagingResponseDto dto
	) {
		return new SearchCrewMemberPagingResponseDto(
			dto.content().stream().map(GetSpecificCrewMemberResponseDto::from).toList(),
			dto.totalPages(),
			dto.totalElements(),
			dto.currentPage(),
			dto.size(),
			dto.first(),
			dto.last()
		);
	}
}
