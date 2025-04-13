package com._42195km.msa.competitionservice.presentation.dto.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record GetRequestDto(
	@Min(0) @NotNull @Parameter(example = "'0'") int page,
	@NotNull @Parameter(example = "'10'") int size
) {
	public GetRequestDto {
		if(size !=0 && size != 30 && size != 50 ){
			size =10;
		}
	}

	public Pageable toPageable(){
		return PageRequest.of(page,size, Sort.by(Sort.Direction.DESC, "createdAt"));
	}
}
