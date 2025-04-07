package com._42195km.msa.crew.presentation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com._42195km.msa.common.api.ApiResponse;
import com._42195km.msa.common.exception.code.CommonErrorCode;
import com._42195km.msa.crew.application.dto.request.CreatePostCommandDto;
import com._42195km.msa.crew.application.service.BoardService;
import com._42195km.msa.crew.presentation.dto.request.CreatePostRequestDto;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/crews/posts")
@RequiredArgsConstructor
public class BoardController {

	private final BoardService boardService;

	@PostMapping("")
	@Operation(summary = "게시글 생성")
	public ResponseEntity<?> createBoard(@RequestBody CreatePostRequestDto requestDto) {
		CreatePostCommandDto commandDto = requestDto.toCommandDto();
		boardService.createPost(commandDto);
		return ResponseEntity.ok(new ApiResponse<>(
			CommonErrorCode.CREW_BOARD_CREATE_POST_SUCCESS.getCode(),
			"게시글 생성에 성공했습니다.",
			CommonErrorCode.CREW_BOARD_CREATE_POST_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@PutMapping("")
	@Operation(summary = "게시글 수정")
	public ResponseEntity<?> updateBoard() {
		return ResponseEntity.ok().build();
	}

	@GetMapping("")
	@Operation(summary = "게시글 목록 조회")
	public ResponseEntity<?> getBoards() {
		return ResponseEntity.ok().build();
	}

	@GetMapping("/search")
	@Operation(summary = "게시글 검색")
	public ResponseEntity<?> searchBoard(@RequestParam int id) {
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{postId}")
	@Operation(summary = "게시글 단건 조회")
	public ResponseEntity<?> getBoard(@PathVariable("postId") int id) {
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/{postId}")
	@Operation(summary = "게시글 삭제")
	public ResponseEntity deleteBoard(@PathVariable("postId") int id) {
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{postId}/comments")
	@Operation(summary = "댓글 등록")
	public ResponseEntity createComment(@PathVariable("postId") int id) {
		return ResponseEntity.ok().build();
	}

	@PutMapping("/{postId}/comments")
	@Operation(summary = "댓글 수정")
	public ResponseEntity updateComment(@PathVariable("postId") int id) {
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/{postId}/comments")
	@Operation(summary = "댓글 삭제")
	public ResponseEntity deleteComment(@PathVariable("postId") int id) {
		return ResponseEntity.ok().build();
	}

}
