package com._42195km.msa.crew.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com._42195km.msa.crew.domain.model.Comment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentAppResponseDto {
	private UUID id;
	private String comment;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static CommentAppResponseDto fromEntity(Comment comment) {
		return new CommentAppResponseDto(
			comment.getId(),
			comment.getComment(),
			comment.getCreatedAt(),
			comment.getUpdatedAt()
		);
	}
}