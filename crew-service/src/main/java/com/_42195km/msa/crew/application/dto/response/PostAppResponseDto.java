package com._42195km.msa.crew.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com._42195km.msa.crew.domain.model.Post;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostAppResponseDto {
	private UUID id;
	private String title;
	private String content;
	private String hashtag;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static PostAppResponseDto fromEntity(Post post) {
		return new PostAppResponseDto(
			post.getId(),
			post.getTitle(),
			post.getContent(),
			post.getHashtag(),
			post.getCreatedAt(),
			post.getUpdatedAt()
		);
	}
}
