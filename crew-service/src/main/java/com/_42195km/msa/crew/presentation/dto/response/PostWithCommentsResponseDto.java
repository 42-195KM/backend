package com._42195km.msa.crew.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com._42195km.msa.crew.application.dto.response.PostWithCommentsAppResponseDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostWithCommentsResponseDto {
	private UUID id;
	private String title;
	private String content;
	private String hashtag;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private List<CommentResponseDto> comments;

	public static PostWithCommentsResponseDto fromApplicationDto(
		com._42195km.msa.crew.application.dto.response.PostWithCommentsAppResponseDto appDto,
		List<CommentResponseDto> commentResponses) {
		return PostWithCommentsResponseDto.builder()
			.id(appDto.getId())
			.title(appDto.getTitle())
			.content(appDto.getContent())
			.hashtag(appDto.getHashtag())
			.createdAt(appDto.getCreatedAt())
			.updatedAt(appDto.getUpdatedAt())
			.comments(commentResponses)
			.build();
	}

	public static PostWithCommentsResponseDto fromApplicationDto(PostWithCommentsAppResponseDto appDto) {
		List<CommentResponseDto> commentResponses = appDto.getComments().stream()
			.map(CommentResponseDto::fromApplicationDto)
			.collect(Collectors.toList());
		return PostWithCommentsResponseDto.builder()
			.id(appDto.getId())
			.title(appDto.getTitle())
			.content(appDto.getContent())
			.hashtag(appDto.getHashtag())
			.createdAt(appDto.getCreatedAt())
			.updatedAt(appDto.getUpdatedAt())
			.comments(commentResponses)
			.build();
	}
}