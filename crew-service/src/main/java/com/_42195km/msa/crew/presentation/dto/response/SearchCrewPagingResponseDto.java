package com._42195km.msa.crew.presentation.dto.response;

import java.util.List;
import java.util.UUID;

import com._42195km.msa.crew.application.dto.response.SearchCrewAppPagingResponseDto;

public record SearchCrewPagingResponseDto(
	List<SearchCrewResponseDto> content,
	int totalPages,
	long totalElements,
	int currentPage,
	int size,
	boolean first,
	boolean last
) {
	public record SearchCrewResponseDto(
		UUID id,
		String name,
		String description,
		UUID captainId,
		Integer capacity,
		Boolean isAutoAgree
	) {
	}


	public static SearchCrewPagingResponseDto from(SearchCrewAppPagingResponseDto dto) {
		return new SearchCrewPagingResponseDto(
			dto.content().stream().map(crew -> new SearchCrewResponseDto(
				crew.id(),
				crew.name(),
				crew.description(),
				crew.captainId(),
				crew.capacity(),
				crew.isAutoAgree()
			)).toList(),
			dto.totalPages(),
			dto.totalElements(),
			dto.currentPage(),
			dto.size(),
			dto.first(),
			dto.last()
		);
	}
}
