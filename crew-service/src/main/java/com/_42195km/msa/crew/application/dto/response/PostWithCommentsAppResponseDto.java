package com._42195km.msa.crew.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com._42195km.msa.crew.domain.model.Post;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostWithCommentsAppResponseDto {
	private UUID id;
	private String title;
	private String content;
	private String hashtag;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private List<CommentAppResponseDto> comments;

	public static PostWithCommentsAppResponseDto fromEntity(Post post, List<CommentAppResponseDto> commentDtos) {
		return new PostWithCommentsAppResponseDto(
			post.getId(),
			post.getTitle(),
			post.getContent(),
			post.getHashtag(),
			post.getCreatedAt(),
			post.getUpdatedAt(),
			commentDtos
		);
	}
}
