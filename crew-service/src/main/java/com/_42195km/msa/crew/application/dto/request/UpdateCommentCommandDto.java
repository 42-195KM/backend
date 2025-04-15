package com._42195km.msa.crew.application.dto.request;

import lombok.Getter;

@Getter
public class UpdateCommentCommandDto {
	private String comment;

	public UpdateCommentCommandDto(String comment) {
		this.comment = comment;
	}
}
