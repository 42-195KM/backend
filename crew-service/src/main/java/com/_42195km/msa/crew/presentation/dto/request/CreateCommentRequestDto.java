package com._42195km.msa.crew.presentation.dto.request;

import com._42195km.msa.crew.application.dto.request.CreateCommentCommandDto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateCommentRequestDto {
	private String comment;

	public CreateCommentCommandDto toCommandDto() {
		return new CreateCommentCommandDto(comment);
	}
}
