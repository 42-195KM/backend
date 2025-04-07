package com._42195km.msa.crew.presentation.dto.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SearchBoardRequestDto(
	@Min(0) @NotNull @Parameter(example = "0", description = "페이지 번호") int page,
	@NotNull @Parameter(example = "10", description = "페이지 사이즈 (허용: 10, 30, 50)") int size,
	@Parameter(description = "검색 키워드", example = "정규런") String keyword
) {
	public Pageable toPageable() {
		// 허용된 사이즈가 아니면 기본값 10 적용
		int allowedSize = (size == 10 || size == 30 || size == 50) ? size : 10;
		return PageRequest.of(page, allowedSize, Sort.by(Sort.Direction.DESC, "createdAt"));
	}
}