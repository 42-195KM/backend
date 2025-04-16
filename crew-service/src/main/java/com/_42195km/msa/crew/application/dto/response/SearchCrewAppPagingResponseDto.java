package com._42195km.msa.crew.application.dto.response;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com._42195km.msa.crew.domain.model.Crew;

public record SearchCrewAppPagingResponseDto(
	List<SearchCrewAppResponseDto> content,
	int totalPages,
	long totalElements,
	int currentPage,
	int size,
	boolean first,
	boolean last
) {
	public record SearchCrewAppResponseDto(
		UUID id,
		String name,
		String description,
		UUID captainId,
		Integer capacity,
		Boolean isAutoAgree
	) {
	}

	public static SearchCrewAppPagingResponseDto from(Page<Crew> crews) {
		return new SearchCrewAppPagingResponseDto(
			crews.getContent().stream().map(crew -> new SearchCrewAppResponseDto(
				crew.getId(),
				crew.getName(),
				crew.getDescription(),
				crew.getCaptainId(),
				crew.getCapacity(),
				crew.getIsAutoAgree()
			)).toList(),
			crews.getTotalPages(),
			crews.getTotalElements(),
			crews.getNumber(),
			crews.getSize(),
			crews.isFirst(),
			crews.isLast()
		);
	}
}
