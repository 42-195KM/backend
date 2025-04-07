package com._42195km.msa.crew.application.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com._42195km.msa.crew.application.dto.response.CommentAppResponseDto;
import com._42195km.msa.crew.application.dto.response.PostAppResponseDto;
import com._42195km.msa.crew.application.dto.response.PostWithCommentsAppResponseDto;
import com._42195km.msa.crew.domain.model.Post;
import com._42195km.msa.crew.presentation.dto.response.PostResponseDto;

@Component
public class PostMapper {
	/**
	 * [1] 엔티티를 앱 DTO로 변환
	 */
	public PostAppResponseDto toAppResponseDto(Post post) {
		return new PostAppResponseDto(
			post.getId(),
			post.getTitle(),
			post.getContent(),
			post.getHashtag(),
			post.getCreatedAt(),
			post.getUpdatedAt()
		);
	}

	/**
	 * [2] 엔티티 Page → 앱 DTO Page
	 */
	public Page<PostAppResponseDto> toAppResponsePage(Page<Post> posts) {
		return posts.map(this::toAppResponseDto);
	}

	/**
	 * [3] 앱 DTO를 프레젠테이션 DTO로 변환
	 */
	public PostResponseDto toPresentationDto(PostAppResponseDto appDto) {
		return PostResponseDto.builder()
			.id(appDto.getId())
			.title(appDto.getTitle())
			.content(appDto.getContent())
			.hashtag(appDto.getHashtag())
			.createdAt(appDto.getCreatedAt())
			.updatedAt(appDto.getUpdatedAt())
			.build();
	}

	/**
	 * [4] 앱 DTO Page → 프레젠테이션 DTO Page
	 */
	public Page<PostResponseDto> toPresentationPage(Page<PostAppResponseDto> appDtoPage) {
		return appDtoPage.map(this::toPresentationDto);
	}

	/**
	 * [5] 엔티티 → 앱 DTO (댓글 포함) 변환
	 */
	public PostWithCommentsAppResponseDto toAppResponseDtoWithComments(Post post, List<CommentAppResponseDto> commentDtos) {
		return PostWithCommentsAppResponseDto.fromEntity(post, commentDtos);
	}
}
