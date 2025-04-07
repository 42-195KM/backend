package com._42195km.msa.crew.presentation.dto.request;

import com._42195km.msa.crew.application.dto.request.CreateCommentCommandDto;

import lombok.Getter;

@Getter
public class CreateCommentRequestDto {
	private String comment;

	public CreateCommentCommandDto toCommandDto() {
		return new CreateCommentCommandDto(comment);
	}
}
