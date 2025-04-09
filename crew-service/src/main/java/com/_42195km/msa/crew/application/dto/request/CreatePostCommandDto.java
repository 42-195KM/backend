package com._42195km.msa.crew.application.dto.request;

import lombok.Getter;

@Getter
public class CreatePostCommandDto {

	private final String title;
	private final String content;
	private final String hashtag;

	public CreatePostCommandDto(String title, String content, String hashtag) {
		this.title = title;
		this.content = content;
		this.hashtag = hashtag;
	}
}
