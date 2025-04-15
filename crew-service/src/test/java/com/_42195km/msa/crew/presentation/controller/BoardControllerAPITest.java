package com._42195km.msa.crew.presentation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BoardControllerAPITest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("게시글 생성 API 테스트")
	void testCreateBoard() throws Exception {
		String createBoardJson = "{\"title\": \"Spring boot API 테스트 코드\", \"content\": \"테스트 코드 게시글\", \"hashtag\": \"Test\"}";
		mockMvc.perform(post("/api/v1/crews/posts")
				.contentType(MediaType.APPLICATION_JSON)
				.content(createBoardJson.getBytes(StandardCharsets.UTF_8)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("게시글 생성에 성공했습니다."));
	}

	@Test
	@DisplayName("게시글 목록 조회 API 테스트")
	void testGetBoards() throws Exception {
		// 게시글 생성
		String createBoardJson = "{\"title\": \"Spring boot API 테스트 코드 2\", \"content\": \"테스트 코드 게시글 2\", \"hashtag\": \"Test2\"}";
		mockMvc.perform(post("/api/v1/crews/posts")
				.contentType(MediaType.APPLICATION_JSON)
				.content(createBoardJson.getBytes(StandardCharsets.UTF_8)))
			.andExpect(status().isOk());

		// 게시글 목록 조회
		MvcResult result = mockMvc.perform(get("/api/v1/crews/posts")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content[0].title").value("Spring boot API 테스트 코드 2"))
			.andReturn();
	}

	@Test
	@DisplayName("게시글 수정 API 테스트")
	void testUpdateBoard() throws Exception {
		// 게시글 생성
		String createBoardJson = "{\"title\": \"수정 전 게시글 \", \"content\": \"수정 전 게시글 생성\", \"hashtag\": \"adj\"}";
		mockMvc.perform(post("/api/v1/crews/posts")
				.contentType(MediaType.APPLICATION_JSON)
				.content(createBoardJson.getBytes(StandardCharsets.UTF_8)))
			.andExpect(status().isOk());

		// 게시글 목록 조회 후 첫 게시글 id 추출 (update API에서는 PathVariable로 해당 id를 사용)
		MvcResult listResult = mockMvc.perform(get("/api/v1/crews/posts")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andReturn();
		String listResponse = listResult.getResponse().getContentAsString();
		String boardId = JsonPath.read(listResponse, "$.data.content[0].id");

		// update API 호출 (경로 변수는 board id를 사용)
		String updateJson = "{\"title\": \"수정 후 게시글\", \"content\": \"수정 된 게시글\", \"hashtag\": \"adj\"}";
		mockMvc.perform(put("/api/v1/crews/posts/" + boardId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(updateJson.getBytes(StandardCharsets.UTF_8)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("게시글 수정 성공했습니다."));
	}

	@Test
	@DisplayName("게시글 단건 조회 API 테스트")
	void testGetBoard() throws Exception {
		// 게시글 목록 조회 후 id 추출
		MvcResult listResult = mockMvc.perform(get("/api/v1/crews/posts")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andReturn();
		String listResponse = listResult.getResponse().getContentAsString();
		String boardId = JsonPath.read(listResponse, "$.data.content[0].id");

		// 단건 조회 API 호출 및 title 검증
		mockMvc.perform(get("/api/v1/crews/posts/" + boardId))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("게시글 삭제 API 테스트")
	void testDeleteBoard() throws Exception {
		// 게시글 생성
		String createBoardJson = "{\"title\": \"삭제용 게시글\", \"content\": \"삭제용\", \"hashtag\": \"delete\"}";
		mockMvc.perform(post("/api/v1/crews/posts")
				.contentType(MediaType.APPLICATION_JSON)
				.content(createBoardJson.getBytes(StandardCharsets.UTF_8)))
			.andExpect(status().isOk());

		// 게시글 목록 조회 후 id 추출
		MvcResult listResult = mockMvc.perform(get("/api/v1/crews/posts")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andReturn();
		String listResponse = listResult.getResponse().getContentAsString();
		String boardId = JsonPath.read(listResponse, "$.data.content[0].id");

		// 삭제 API 호출 (PATCH 요청)
		mockMvc.perform(patch("/api/v1/crews/posts/" + boardId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("게시글 삭제 성공했습니다."));
	}

	@Test
	@DisplayName("댓글 생성 API 테스트")
	void testCreateComment() throws Exception {

		// 게시글 목록 조회 후 id 추출
		MvcResult listResult = mockMvc.perform(get("/api/v1/crews/posts")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andReturn();
		String listResponse = listResult.getResponse().getContentAsString();
		String boardId = JsonPath.read(listResponse, "$.data.content[0].id");

		// 댓글 생성 API 호출
		String createCommentJson = "{\"comment\": \"댓글 생성 테스트\"}";
		mockMvc.perform(post("/api/v1/crews/posts/" + boardId + "/comments")
				.contentType(MediaType.APPLICATION_JSON)
				.content(createCommentJson.getBytes(StandardCharsets.UTF_8)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("댓글 생성에 성공했습니다."));
	}

	@Test
	@DisplayName("댓글 수정 API 테스트")
	void testUpdateComment() throws Exception {
		// 게시글 목록 조회 후 id 추출
		MvcResult listResult = mockMvc.perform(get("/api/v1/crews/posts")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andReturn();
		String listResponse = listResult.getResponse().getContentAsString();
		String boardId = JsonPath.read(listResponse, "$.data.content[0].id");

		// 댓글 생성
		String createCommentJson = "{\"comment\": \"댓글 생성 테스트 -  수정 전\"}";
		mockMvc.perform(post("/api/v1/crews/posts/" + boardId + "/comments")
				.contentType(MediaType.APPLICATION_JSON)
				.content(createCommentJson.getBytes(StandardCharsets.UTF_8)))
			.andExpect(status().isOk());

		// 단건 게시글 조회로 댓글 id 추출
		MvcResult detailResult = mockMvc.perform(get("/api/v1/crews/posts/" + boardId))
			.andExpect(status().isOk())
			.andReturn();
		String detailResponse = detailResult.getResponse().getContentAsString();
		String commentId = JsonPath.read(detailResponse, "$.data.comments[0].id");

		// 댓글 수정 API 호출
		String updateCommentJson = "{\"comment\": \"수정된 댓글\"}";
		mockMvc.perform(put("/api/v1/crews/posts/" + commentId + "/comments")
				.contentType(MediaType.APPLICATION_JSON)
				.content(updateCommentJson.getBytes(StandardCharsets.UTF_8)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("댓글 수정에 성공했습니다."));
	}

	@Test
	@DisplayName("댓글 삭제 API 테스트")
	void testDeleteComment() throws Exception {
		// 게시글 목록 조회 후 id 추출
		MvcResult listResult = mockMvc.perform(get("/api/v1/crews/posts")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andReturn();
		String listResponse = listResult.getResponse().getContentAsString();
		String boardId = JsonPath.read(listResponse, "$.data.content[0].id");

		// 댓글 생성
		String createCommentJson = "{\"comment\": \"삭제용 댓글\"}";
		mockMvc.perform(post("/api/v1/crews/posts/" + boardId + "/comments")
				.contentType(MediaType.APPLICATION_JSON)
				.content(createCommentJson.getBytes(StandardCharsets.UTF_8)))
			.andExpect(status().isOk());

		// 단건 게시글 조회로 댓글 id 추출
		MvcResult detailResult = mockMvc.perform(get("/api/v1/crews/posts/" + boardId))
			.andExpect(status().isOk())
			.andReturn();
		String detailResponse = detailResult.getResponse().getContentAsString();
		String commentId = JsonPath.read(detailResponse, "$.data.comments[0].id");

		// 댓글 삭제 API 호출
		mockMvc.perform(patch("/api/v1/crews/posts/" + commentId + "/comments"))
			.andExpect(status().isOk());
	}

}