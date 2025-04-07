package com._42195km.msa.crew.application.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com._42195km.msa.common.exception.CustomBusinessException;
import com._42195km.msa.common.exception.code.CommonErrorCode;
import com._42195km.msa.crew.application.dto.request.CreatePostCommandDto;
import com._42195km.msa.crew.application.dto.request.UpdatePostCommandDto;
import com._42195km.msa.crew.application.dto.response.PostAppResponseDto;
import com._42195km.msa.crew.application.mapper.PostMapper;
import com._42195km.msa.crew.domain.model.Post;
import com._42195km.msa.crew.domain.repository.PostRepository;
import com._42195km.msa.crew.presentation.dto.request.SearchBoardRequestDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {

	private final PostRepository postRepository;
	private final PostMapper postMapper;

	public void createPost(CreatePostCommandDto commandDto) {
		try {
			Post post = Post.create(commandDto);
			postRepository.save(post);
		} catch (Exception e) {
			throw CustomBusinessException.from(CommonErrorCode.CREW_BOARD_CREATE_POST_FAILED);
		}
	}

	@Transactional
	public void updatePost(UUID userId, UpdatePostCommandDto commandDto) {
		Post post = postRepository.findByIdAndIsDeletedFalse(userId)
			.orElseThrow(() -> CustomBusinessException.from(CommonErrorCode.CREW_BOARD_GET_POST_FAILED));

		try {
			post.update(commandDto);
		} catch (Exception e) {
			throw CustomBusinessException.from(CommonErrorCode.CREW_BOARD_UPDATE_POST_FAILED);
		}
	}

	public Page<PostAppResponseDto> getPosts(Pageable pageable) {
		try {
			Page<Post> posts = postRepository.findAll(pageable);
			return postMapper.toAppResponsePage(posts);
		} catch (Exception e) {
			throw CustomBusinessException.from(CommonErrorCode.CREW_BOARD_GET_POST_FAILED);
		}

	}

	public Page<PostAppResponseDto> searchBoard(String keyword, Pageable pageable) {
		try {
			Page<Post> posts = postRepository.searchPosts(keyword, pageable);
			return postMapper.toAppResponsePage(posts);
		} catch (Exception e) {
			throw CustomBusinessException.from(CommonErrorCode.CREW_BOARD_SEARCH_POST_FAILED);
		}
	}

	/**
	 * TODO : 댓글도 포함해야 함
	 * @param postId
	 * @return
	 */
	public PostAppResponseDto getPost(UUID postId) {
		Post post = postRepository.findByIdAndIsDeletedFalse(postId)
			.orElseThrow(() -> CustomBusinessException.from(CommonErrorCode.CREW_BOARD_GET_POST_FAILED));
		return postMapper.toAppResponseDto(post);
	}

	public void deletePost(UUID postId) {
		Post post = postRepository.findByIdAndIsDeletedFalse(postId)
			.orElseThrow(() -> CustomBusinessException.from(CommonErrorCode.CREW_BOARD_GET_POST_FAILED));

	}
}
