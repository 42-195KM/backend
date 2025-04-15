package com._42195km.msa.crew.application.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import com._42195km.msa.crew.domain.model.CrewMemberMapping;

public record SearchCrewMemberAppPagingResponseDto(
	List<GetSpecificCrewMemberAppResponseDto> content,
	int totalPages,
	long totalElements,
	int currentPage,
	int size,
	boolean first,
	boolean last
) {
	public static SearchCrewMemberAppPagingResponseDto from(Page<CrewMemberMapping> memberMappings) {
		return new SearchCrewMemberAppPagingResponseDto(
			memberMappings.getContent().stream().map(GetSpecificCrewMemberAppResponseDto::from).toList(),
			memberMappings.getTotalPages(),
			memberMappings.getTotalElements(),
			memberMappings.getNumber(),
			memberMappings.getSize(),
			memberMappings.isFirst(),
			memberMappings.isLast()
		);
	}
}
