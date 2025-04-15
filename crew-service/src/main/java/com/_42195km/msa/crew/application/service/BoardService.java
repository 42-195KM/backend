package com._42195km.msa.crew.application.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com._42195km.msa.common.exception.CustomBusinessException;
import com._42195km.msa.crew.application.dto.request.CreateCommentCommandDto;
import com._42195km.msa.crew.application.dto.request.CreatePostCommandDto;
import com._42195km.msa.crew.application.dto.request.UpdateCommentCommandDto;
import com._42195km.msa.crew.application.dto.request.UpdatePostCommandDto;
import com._42195km.msa.crew.application.dto.response.CommentAppResponseDto;
import com._42195km.msa.crew.application.dto.response.PostAppResponseDto;
import com._42195km.msa.crew.application.dto.response.PostWithCommentsAppResponseDto;
import com._42195km.msa.crew.application.mapper.PostMapper;
import com._42195km.msa.crew.domain.model.Comment;
import com._42195km.msa.crew.domain.model.Post;
import com._42195km.msa.crew.infrastructure.persistence.CommentRepositoryImpl;
import com._42195km.msa.crew.infrastructure.persistence.PostRepositoryImpl;
import com._42195km.msa.crew.application.exception.BoardServiceCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {

	private final PostRepositoryImpl postRepository;
	private final CommentRepositoryImpl commentRepository;
	private final PostMapper postMapper;

	public void createPost(CreatePostCommandDto commandDto) {
		try {
			Post post = Post.create(commandDto);
			postRepository.save(post);
		} catch (Exception e) {
			throw CustomBusinessException.from(BoardServiceCode.CREW_BOARD_CREATE_POST_FAILED);
		}
	}

	@Transactional
	public void updatePost(UUID userId, UpdatePostCommandDto commandDto) {
		Post post = postRepository.findById(userId)
			.orElseThrow(() -> CustomBusinessException.from(BoardServiceCode.CREW_BOARD_GET_POST_FAILED));

		try {
			post.update(commandDto);
		} catch (Exception e) {
			throw CustomBusinessException.from(BoardServiceCode.CREW_BOARD_UPDATE_POST_FAILED);
		}
	}

	public Page<PostAppResponseDto> getPosts(Pageable pageable) {
		try {
			Page<Post> posts = postRepository.findAll(pageable);
			return postMapper.toAppResponsePage(posts);
		} catch (Exception e) {
			throw CustomBusinessException.from(BoardServiceCode.CREW_BOARD_GET_POST_FAILED);
		}

	}

	public Page<PostAppResponseDto> searchBoard(String keyword, Pageable pageable) {
		try {
			Page<Post> posts = postRepository.searchPosts(keyword, pageable);
			return postMapper.toAppResponsePage(posts);
		} catch (Exception e) {
			throw CustomBusinessException.from(BoardServiceCode.CREW_BOARD_SEARCH_POST_FAILED);
		}
	}

	/**
	 * TODO : 댓글도 포함해야 함
	 * @param postId
	 * @return
	 */
	public PostWithCommentsAppResponseDto getPost(UUID postId) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> CustomBusinessException.from(BoardServiceCode.CREW_BOARD_GET_POST_FAILED));

		List<Comment> comments = commentRepository.findByPostId(postId);
		List<CommentAppResponseDto> commentDtos = comments.stream()
			.map(CommentAppResponseDto::fromEntity)
			.collect(Collectors.toList());

		return postMapper.toAppResponseDtoWithComments(post, commentDtos);
	}

	public void deletePost(UUID postId) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> CustomBusinessException.from(BoardServiceCode.CREW_BOARD_GET_POST_FAILED));

	}

	public void createComment(UUID postId, CreateCommentCommandDto commandDto) {
		// 게시글이 존재하는지 확인하는 용도
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> CustomBusinessException.from(BoardServiceCode.CREW_BOARD_GET_POST_FAILED));

		try {
			Comment comment = Comment.create(postId, commandDto);
			commentRepository.save(comment);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Transactional
	public void updateComment(UUID commentId, UpdateCommentCommandDto commandDto) {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> CustomBusinessException.from(BoardServiceCode.CREW_BOARD_GET_COMMENT_FAILED));

		try {
			comment.update(commandDto);
		} catch (Exception e) {
			throw CustomBusinessException.from(BoardServiceCode.CREW_BOARD_UPDATE_COMMENT_FAILED);
		}

	}

	public void deleteComment(UUID commentId) {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> CustomBusinessException.from(BoardServiceCode.CREW_BOARD_GET_COMMENT_FAILED));
	}
}
