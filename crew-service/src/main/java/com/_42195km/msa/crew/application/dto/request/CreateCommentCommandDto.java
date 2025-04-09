package com._42195km.msa.crew.application.dto.request;

import lombok.Getter;

@Getter
public class CreateCommentCommandDto {
	private String comment;

	public CreateCommentCommandDto(String comment) {
		this.comment = comment;
	}
}
