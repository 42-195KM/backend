package com._42195km.msa.crew.presentation.controller;

import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._42195km.msa.common.api.ApiResponse;
import com._42195km.msa.common.exception.code.CommonErrorCode;
import com._42195km.msa.crew.application.dto.response.PostAppResponseDto;
import com._42195km.msa.crew.application.dto.response.PostWithCommentsAppResponseDto;
import com._42195km.msa.crew.application.mapper.PostMapper;
import com._42195km.msa.crew.application.service.BoardService;
import com._42195km.msa.crew.presentation.dto.request.CreateCommentRequestDto;
import com._42195km.msa.crew.presentation.dto.request.CreatePostRequestDto;
import com._42195km.msa.crew.presentation.dto.request.GetBoardRequestDto;
import com._42195km.msa.crew.presentation.dto.request.SearchBoardRequestDto;
import com._42195km.msa.crew.presentation.dto.request.UpdateCommentRequestDto;
import com._42195km.msa.crew.presentation.dto.request.UpdatePostRequestDto;
import com._42195km.msa.crew.presentation.dto.response.PostResponseDto;
import com._42195km.msa.crew.presentation.dto.response.PostWithCommentsResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/crews/posts")
@RequiredArgsConstructor
public class BoardController {

	private final BoardService boardService;
	private final PostMapper postMapper;

	@PostMapping("")
	@Operation(summary = "게시글 생성")
	public ResponseEntity<?> createBoard(@RequestBody CreatePostRequestDto requestDto) {
		boardService.createPost(requestDto.toCommandDto());
		return ResponseEntity.ok(new ApiResponse<>(
			CommonErrorCode.CREW_BOARD_CREATE_POST_SUCCESS.getCode(),
			"게시글 생성에 성공했습니다.",
			CommonErrorCode.CREW_BOARD_CREATE_POST_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@PutMapping("/{userId}")
	@Operation(summary = "게시글 수정")
	public ResponseEntity<?> updateBoard(@PathVariable("userId") UUID userId,
		@RequestBody UpdatePostRequestDto requestDto) {
		boardService.updatePost(userId, requestDto.toCommandDto());
		return ResponseEntity.ok(new ApiResponse<>(
			CommonErrorCode.CREW_BOARD_UPDATE_POST_SUCCESS.getCode(),
			"게시글 수정 성공했습니다.",
			CommonErrorCode.CREW_BOARD_UPDATE_POST_SUCCESS.getMessage(),
			HttpStatus.ACCEPTED.value()));
	}

	@GetMapping("")
	@Operation(summary = "게시글 목록 조회")
	public ResponseEntity<?> getBoards(@ModelAttribute @Valid GetBoardRequestDto requestDto) {
		Page<PostAppResponseDto> posts = boardService.getPosts(requestDto.toPageable());
		Page<PostResponseDto> presentationPage = postMapper.toPresentationPage(posts);
		return ResponseEntity.ok(new ApiResponse<>(
			CommonErrorCode.CREW_BOARD_GET_POST_SUCCESS.getCode(),
			presentationPage,
			CommonErrorCode.CREW_BOARD_GET_POST_SUCCESS.getMessage(),
			HttpStatus.ACCEPTED.value()));
	}

	@GetMapping("/search")
	@Operation(summary = "게시글 검색")
	public ResponseEntity<?> searchBoard(@ParameterObject SearchBoardRequestDto requestDto) {
		Page<PostAppResponseDto> posts = boardService.searchBoard(requestDto.keyword(), requestDto.toPageable());
		Page<PostResponseDto> presentationPage = postMapper.toPresentationPage(posts);
		return ResponseEntity.ok(new ApiResponse<>(
			CommonErrorCode.CREW_BOARD_SEARCH_POST_SUCCESS.getCode(),
			presentationPage,
			CommonErrorCode.CREW_BOARD_SEARCH_POST_SUCCESS.getMessage(),
			HttpStatus.ACCEPTED.value()));
	}

	@GetMapping("/{postId}")
	@Operation(summary = "게시글 단건 조회")
	public ResponseEntity<?> getBoard(@PathVariable("postId") UUID postId) {
		PostWithCommentsAppResponseDto post = boardService.getPost(postId);
		PostWithCommentsResponseDto presentationPost = PostWithCommentsResponseDto.fromApplicationDto(post);

		return ResponseEntity.ok(new ApiResponse<>(
			CommonErrorCode.CREW_BOARD_GET_POST_SUCCESS.getCode(),
			presentationPost,
			CommonErrorCode.CREW_BOARD_GET_POST_SUCCESS.getMessage(),
			HttpStatus.ACCEPTED.value()));
	}

	/**
	 * TODO : 인증/인가 구현 후 soft delete로 구현
	 * @param postId
	 * @return
	 */
	@PatchMapping("/{postId}")
	@Operation(summary = "게시글 삭제")
	public ResponseEntity deleteBoard(@PathVariable("postId") UUID postId) {
		boardService.deletePost(postId);
		return ResponseEntity.ok(new ApiResponse<>(
			CommonErrorCode.CREW_BOARD_GET_POST_SUCCESS.getCode(),
			"게시글 삭제 성공했습니다.",
			CommonErrorCode.CREW_BOARD_GET_POST_SUCCESS.getMessage(),
			HttpStatus.ACCEPTED.value()));
	}

	@PostMapping("/{postId}/comments")
	@Operation(summary = "댓글 등록")
	public ResponseEntity createComment(@PathVariable("postId") UUID postId,
		@RequestBody CreateCommentRequestDto requestDto) {
		boardService.createComment(postId, requestDto.toCommandDto());
		return ResponseEntity.ok(new ApiResponse<>(
			CommonErrorCode.CREW_BOARD_CREATE_COMMENT_SUCCESS.getCode(),
			"댓글 생성에 성공했습니다.",
			CommonErrorCode.CREW_BOARD_CREATE_COMMENT_SUCCESS.getMessage(),
			HttpStatus.ACCEPTED.value()));
	}

	@PutMapping("/{commentId}/comments")
	@Operation(summary = "댓글 수정")
	public ResponseEntity updateComment(@PathVariable("commentId") UUID commentId,
		@RequestBody UpdateCommentRequestDto requestDto) {
		boardService.updateComment(commentId, requestDto.toCommandDto());
		return ResponseEntity.ok(new ApiResponse<>(
			CommonErrorCode.CREW_BOARD_UPDATE_COMMENT_SUCCESS.getCode(),
			"댓글 수정에 성공했습니다.",
			CommonErrorCode.CREW_BOARD_UPDATE_COMMENT_SUCCESS.getMessage(),
			HttpStatus.ACCEPTED.value()));
	}

	/**
	 * TODO : 인증/인가 구현 후 soft delete로 구현
	 * @param commentId
	 * @return
	 */
	@PatchMapping("/{commentId}/comments")
	@Operation(summary = "댓글 삭제")
	public ResponseEntity deleteComment(@PathVariable("commentId") UUID commentId) {
		boardService.deleteComment(commentId);
		return ResponseEntity.ok().build();
	}

}
