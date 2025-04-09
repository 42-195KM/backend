package com._42195km.msa.crew.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com._42195km.msa.crew.application.dto.response.PostAppResponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PostResponseDto {
	private UUID id;
	private String title;
	private String content;
	private String hashtag;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static PostResponseDto fromApplicationDto(PostAppResponseDto appDto) {
		return PostResponseDto.builder()
			.id(appDto.getId())
			.title(appDto.getTitle())
			.content(appDto.getContent())
			.hashtag(appDto.getHashtag())
			.createdAt(appDto.getCreatedAt())
			.updatedAt(appDto.getUpdatedAt())
			.build();
	}
}
