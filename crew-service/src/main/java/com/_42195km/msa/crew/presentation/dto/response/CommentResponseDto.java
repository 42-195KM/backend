package com._42195km.msa.crew.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com._42195km.msa.crew.application.dto.response.CommentAppResponseDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentResponseDto {
	private UUID id;
	private String comment;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static CommentResponseDto fromApplicationDto(CommentAppResponseDto appDto) {
		return CommentResponseDto.builder()
			.id(appDto.getId())
			.comment(appDto.getComment())
			.createdAt(appDto.getCreatedAt())
			.updatedAt(appDto.getUpdatedAt())
			.build();
	}
}