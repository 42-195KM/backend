package com._42195km.msa.crew.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com._42195km.msa.common.exception.CustomBusinessException;
import com._42195km.msa.common.exception.code.CommonErrorCode;
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

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BoardServiceTest {

	@Mock
	private PostRepositoryImpl postRepository;

	@Mock
	private CommentRepositoryImpl commentRepository;

	@Mock
	private PostMapper postMapper;

	@InjectMocks
	private BoardService boardService;

	private UUID postId;
	private UUID userId;
	private UUID commentId;
	private Post post;
	private Comment comment;
	private CreatePostCommandDto createPostCommandDto;
	private UpdatePostCommandDto updatePostCommandDto;
	private CreateCommentCommandDto createCommentCommandDto;
	private UpdateCommentCommandDto updateCommentCommandDto;
	private List<Post> postList;
	private List<Comment> commentList;
	private PostAppResponseDto postAppResponseDto;
	private CommentAppResponseDto commentAppResponseDto;
	private PostWithCommentsAppResponseDto postWithCommentsAppResponseDto;

	@BeforeEach
	void setUp() {
		// UUIDs 생성
		postId = UUID.randomUUID();
		userId = UUID.randomUUID();
		commentId = UUID.randomUUID();

		// DTOs 생성
		createPostCommandDto = new CreatePostCommandDto("테스트 제목", "테스트 내용", "테스트태그");
		updatePostCommandDto = new UpdatePostCommandDto("수정 테스트 제목", "수정 테스트 내용", "수정 테스트태그");
		createCommentCommandDto = new CreateCommentCommandDto("댓글 생성 테스트");
		updateCommentCommandDto = new UpdateCommentCommandDto("댓글 수정 테스트");

		// Setup Post entity
		post = mock(Post.class);
		when(post.getId()).thenReturn(postId);
		when(post.isDeleted()).thenReturn(false);

		// Setup Comment entity
		comment = mock(Comment.class);
		when(comment.getId()).thenReturn(commentId);
		when(comment.getPostId()).thenReturn(postId);
		when(comment.isDeleted()).thenReturn(false);

		// Setup collections
		postList = new ArrayList<>(Arrays.asList(post));
		commentList = new ArrayList<>(Arrays.asList(comment));

		// Setup response DTOs
		postAppResponseDto = mock(PostAppResponseDto.class);
		commentAppResponseDto = mock(CommentAppResponseDto.class);
		postWithCommentsAppResponseDto = mock(PostWithCommentsAppResponseDto.class);
	}

	@Test
	@DisplayName("게시글 생성 성공 테스트")
	void createPost_Success() {
		// Given
		try (MockedStatic<Post> mockedStatic = mockStatic(Post.class)) {
			Post createdPost = mock(Post.class);
			mockedStatic.when(() -> Post.create(any(CreatePostCommandDto.class))).thenReturn(createdPost);

			// When
			boardService.createPost(createPostCommandDto);

			// Then
			verify(postRepository, times(1)).save(any(Post.class));
		}
	}

	@Test
	@DisplayName("게시글 생성 실패 테스트")
	void createPost_Failure() {
		// Given
		try (MockedStatic<Post> mockedStatic = mockStatic(Post.class)) {
			Post createdPost = mock(Post.class);
			mockedStatic.when(() -> Post.create(any(CreatePostCommandDto.class))).thenReturn(createdPost);
			when(postRepository.save(any(Post.class))).thenThrow(new RuntimeException());

			// When & Then
			CustomBusinessException exception = assertThrows(CustomBusinessException.class,
				() -> boardService.createPost(createPostCommandDto));
			assertEquals(CommonErrorCode.CREW_BOARD_CREATE_POST_FAILED, exception.getCode());
		}
	}

	@Test
	@DisplayName("게시글 수정 성공 테스트")
	void updatePost_Success() {
		// Given
		when(postRepository.findById(userId)).thenReturn(Optional.of(post));
		doNothing().when(post).update(any(UpdatePostCommandDto.class));

		// When
		boardService.updatePost(userId, updatePostCommandDto);

		// Then
		verify(post, times(1)).update(updatePostCommandDto);
	}

	@Test
	@DisplayName("게시글 수정 실패 테스트 - 게시글 찾기 실패")
	void updatePost_NotFound() {
		// Given
		when(postRepository.findById(userId)).thenReturn(Optional.empty());

		// When & Then
		CustomBusinessException exception = assertThrows(CustomBusinessException.class,
			() -> boardService.updatePost(userId, updatePostCommandDto));
		assertEquals(CommonErrorCode.CREW_BOARD_GET_POST_FAILED, exception.getCode());
	}

	@Test
	@DisplayName("게시글 수정 실패 테스트 - 업데이트 중 예외 발생")
	void updatePost_UpdateFailed() {
		// Given
		when(postRepository.findById(userId)).thenReturn(Optional.of(post));
		doThrow(new RuntimeException()).when(post).update(any(UpdatePostCommandDto.class));

		// When & Then
		CustomBusinessException exception = assertThrows(CustomBusinessException.class,
			() -> boardService.updatePost(userId, updatePostCommandDto));
		assertEquals(CommonErrorCode.CREW_BOARD_UPDATE_POST_FAILED, exception.getCode());
	}

	@Test
	@DisplayName("게시글 목록 조회 성공 테스트")
	void getPosts_Success() {
		// Given
		Pageable pageable = PageRequest.of(0, 10);
		Page<Post> postPage = new PageImpl<>(postList, pageable, postList.size());
		Page<PostAppResponseDto> postDtoPage = new PageImpl<>(Arrays.asList(postAppResponseDto), pageable, 1);

		when(postRepository.findAll(pageable)).thenReturn(postPage);
		when(postMapper.toAppResponsePage(postPage)).thenReturn(postDtoPage);

		// When
		Page<PostAppResponseDto> result = boardService.getPosts(pageable);

		// Then
		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
		verify(postRepository, times(1)).findAll(pageable);
		verify(postMapper, times(1)).toAppResponsePage(postPage);
	}

	@Test
	@DisplayName("게시글 목록 조회 실패 테스트")
	void getPosts_Failed() {
		// Given
		Pageable pageable = PageRequest.of(0, 10);
		when(postRepository.findAll(pageable)).thenThrow(new RuntimeException());

		// When & Then
		CustomBusinessException exception = assertThrows(CustomBusinessException.class,
			() -> boardService.getPosts(pageable));
		assertEquals(CommonErrorCode.CREW_BOARD_GET_POST_FAILED, exception.getCode());
	}

	@Test
	@DisplayName("게시글 검색 성공 테스트")
	void searchBoard_Success() {
		// Given
		String keyword = "test";
		Pageable pageable = PageRequest.of(0, 10);
		Page<Post> postPage = new PageImpl<>(postList, pageable, postList.size());
		Page<PostAppResponseDto> postDtoPage = new PageImpl<>(Arrays.asList(postAppResponseDto), pageable, 1);

		when(postRepository.searchPosts(eq(keyword), eq(pageable))).thenReturn(postPage);
		when(postMapper.toAppResponsePage(postPage)).thenReturn(postDtoPage);

		// When
		Page<PostAppResponseDto> result = boardService.searchBoard(keyword, pageable);

		// Then
		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
		verify(postRepository, times(1)).searchPosts(keyword, pageable);
		verify(postMapper, times(1)).toAppResponsePage(postPage);
	}

	@Test
	@DisplayName("게시글 검색 실패 테스트")
	void searchBoard_Failed() {
		// Given
		String keyword = "test";
		Pageable pageable = PageRequest.of(0, 10);
		when(postRepository.searchPosts(eq(keyword), eq(pageable))).thenThrow(new RuntimeException());

		// When & Then
		CustomBusinessException exception = assertThrows(CustomBusinessException.class,
			() -> boardService.searchBoard(keyword, pageable));
		assertEquals(CommonErrorCode.CREW_BOARD_SEARCH_POST_FAILED, exception.getCode());
	}

	@Test
	@DisplayName("게시글 단건 조회 성공 테스트")
	void getPost_Success() {
		// Given
		List<CommentAppResponseDto> commentDtos = Arrays.asList(commentAppResponseDto);

		when(postRepository.findById(postId)).thenReturn(Optional.of(post));
		when(commentRepository.findByPostId(postId)).thenReturn(commentList);

		try (MockedStatic<CommentAppResponseDto> mockedCommentDto = mockStatic(CommentAppResponseDto.class)) {
			mockedCommentDto.when(() -> CommentAppResponseDto.fromEntity(any(Comment.class)))
				.thenReturn(commentAppResponseDto);

			when(postMapper.toAppResponseDtoWithComments(eq(post), anyList()))
				.thenReturn(postWithCommentsAppResponseDto);

			// When
			PostWithCommentsAppResponseDto result = boardService.getPost(postId);

			// Then
			assertNotNull(result);
			verify(postRepository, times(1)).findById(postId);
			verify(commentRepository, times(1)).findByPostId(postId);
			verify(postMapper, times(1)).toAppResponseDtoWithComments(eq(post), anyList());
		}
	}

	@Test
	@DisplayName("게시글 단건 조회 실패 테스트 - 게시글 찾기 실패")
	void getPost_NotFound() {
		// Given
		when(postRepository.findById(postId)).thenReturn(Optional.empty());

		// When & Then
		CustomBusinessException exception = assertThrows(CustomBusinessException.class,
			() -> boardService.getPost(postId));
		assertEquals(CommonErrorCode.CREW_BOARD_GET_POST_FAILED, exception.getCode());
	}

	@Test
	@DisplayName("게시글 삭제 성공 테스트")
	void deletePost_Success() {
		// Given
		when(postRepository.findById(postId)).thenReturn(Optional.of(post));

		// When
		boardService.deletePost(postId);

		// Then
		verify(postRepository, times(1)).findById(postId);
		// Note: The actual deletion logic seems to be incomplete in the original code
	}

	@Test
	@DisplayName("게시글 삭제 실패 테스트 - 게시글 찾기 실패")
	void deletePost_NotFound() {
		// Given
		when(postRepository.findById(postId)).thenReturn(Optional.empty());

		// When & Then
		CustomBusinessException exception = assertThrows(CustomBusinessException.class,
			() -> boardService.deletePost(postId));
		assertEquals(CommonErrorCode.CREW_BOARD_GET_POST_FAILED, exception.getCode());
	}

	@Test
	@DisplayName("댓글 생성 성공 테스트")
	void createComment_Success() {
		// Given
		when(postRepository.findById(postId)).thenReturn(Optional.of(post));

		try (MockedStatic<Comment> mockedStatic = mockStatic(Comment.class)) {
			Comment createdComment = mock(Comment.class);
			mockedStatic.when(() -> Comment.create(eq(postId), any(CreateCommentCommandDto.class)))
				.thenReturn(createdComment);

			// When
			boardService.createComment(postId, createCommentCommandDto);

			// Then
			verify(postRepository, times(1)).findById(postId);
			verify(commentRepository, times(1)).save(any(Comment.class));
		}
	}

	@Test
	@DisplayName("댓글 생성 실패 테스트 - 게시글 찾기 실패")
	void createComment_PostNotFound() {
		// Given
		when(postRepository.findById(postId)).thenReturn(Optional.empty());

		// When & Then
		CustomBusinessException exception = assertThrows(CustomBusinessException.class,
			() -> boardService.createComment(postId, createCommentCommandDto));
		assertEquals(CommonErrorCode.CREW_BOARD_GET_POST_FAILED, exception.getCode());
	}

	@Test
	@DisplayName("댓글 생성 실패 테스트 - 댓글 저장 중 예외 발생")
	void createComment_SaveFailed() {
		// Given
		when(postRepository.findById(postId)).thenReturn(Optional.of(post));

		try (MockedStatic<Comment> mockedStatic = mockStatic(Comment.class)) {
			Comment createdComment = mock(Comment.class);
			mockedStatic.when(() -> Comment.create(eq(postId), any(CreateCommentCommandDto.class)))
				.thenReturn(createdComment);
			when(commentRepository.save(any(Comment.class))).thenThrow(new RuntimeException());

			// When & Then
			assertThrows(RuntimeException.class,
				() -> boardService.createComment(postId, createCommentCommandDto));
		}
	}

	@Test
	@DisplayName("댓글 수정 성공 테스트")
	void updateComment_Success() {
		// Given
		when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
		doNothing().when(comment).update(any(UpdateCommentCommandDto.class));

		// When
		boardService.updateComment(commentId, updateCommentCommandDto);

		// Then
		verify(commentRepository, times(1)).findById(commentId);
		verify(comment, times(1)).update(updateCommentCommandDto);
	}

	@Test
	@DisplayName("댓글 수정 실패 테스트 - 댓글 찾기 실패")
	void updateComment_NotFound() {
		// Given
		when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

		// When & Then
		CustomBusinessException exception = assertThrows(CustomBusinessException.class,
			() -> boardService.updateComment(commentId, updateCommentCommandDto));
		assertEquals(CommonErrorCode.CREW_BOARD_GET_COMMENT_FAILED, exception.getCode());
	}

	@Test
	@DisplayName("댓글 수정 실패 테스트 - 업데이트 중 예외 발생")
	void updateComment_UpdateFailed() {
		// Given
		when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
		doThrow(new RuntimeException()).when(comment).update(any(UpdateCommentCommandDto.class));

		// When & Then
		CustomBusinessException exception = assertThrows(CustomBusinessException.class,
			() -> boardService.updateComment(commentId, updateCommentCommandDto));
		assertEquals(CommonErrorCode.CREW_BOARD_UPDATE_COMMENT_FAILED, exception.getCode());
	}

	@Test
	@DisplayName("댓글 삭제 성공 테스트")
	void deleteComment_Success() {
		// Given
		when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

		// When
		boardService.deleteComment(commentId);

		// Then
		verify(commentRepository, times(1)).findById(commentId);
		// Note: The actual deletion logic seems to be incomplete in the original code
	}

	@Test
	@DisplayName("댓글 삭제 실패 테스트 - 댓글 찾기 실패")
	void deleteComment_NotFound() {
		// Given
		when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

		// When & Then
		CustomBusinessException exception = assertThrows(CustomBusinessException.class,
			() -> boardService.deleteComment(commentId));
		assertEquals(CommonErrorCode.CREW_BOARD_GET_COMMENT_FAILED, exception.getCode());
	}
}