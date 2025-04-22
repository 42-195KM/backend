package com._42195km.msa.runningrecordservice;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;


/**
 * RunningRecord 모듈의 전체 흐름(생성, 조회, 목록, 검색, 삭제)을 검증하는
 * 통합 테스트 예제입니다.
 *
 * 이 테스트는 presentation 계층(RunningRecordController, DTO),
 * application 계층(RunningRecordService, RunningRecordServiceImpl),
 * domain 계층(RunningRecord, RunningRecordRepository) 및
 * infrastructure 계층(RunningRecordJpaRepository, RunningRecordServiceCode)을 모두 고려합니다.
 *
 * 참고:
 * - CreateRunningRecordRequestDto :contentReference[oaicite:0]{index=0}
 * - CreateRunningRecordResponseDto :contentReference[oaicite:1]{index=1}
 * - DeleteRunningRecordResponseDto :contentReference[oaicite:2]{index=2}
 * - GetRunningRecordResponseDto :contentReference[oaicite:3]{index=3}
 * - RunningRecordController :contentReference[oaicite:4]{index=4}
 * - RunningRecordJpaRepository :contentReference[oaicite:5]{index=5}
 * - RunningRecordServiceCode :contentReference[oaicite:6]{index=6}
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "testUser", roles = "MASTER")
class RunningRecordServiceIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper; // JSON 직렬화/역직렬화를 위한 ObjectMapper

	private final String BASE_URL = "/api/v1/running-records";

	@Test
	void testRunningRecordCrudFlowIntegration() throws Exception {
		// 1. 기록 생성 (POST)
		UUID userId = UUID.randomUUID();
		Map<String, Object> createPayload = new HashMap<>();
		createPayload.put("userId", userId.toString());
		createPayload.put("distance", 10.0);
		createPayload.put("timer", Duration.ofMinutes(1));
		createPayload.put("pace", 6.0);

		String createRequestJson = objectMapper.writeValueAsString(createPayload);

		MvcResult postResult = mockMvc.perform(
				post(BASE_URL)
					.contentType(MediaType.APPLICATION_JSON)
					.content(createRequestJson)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("RUNNING_RECORD_CREATE_SUCCESS"))
			.andExpect(jsonPath("$.data.id").exists())
			.andReturn();

		// 생성 응답에서 id 추출
		String postResponse = postResult.getResponse().getContentAsString();
		JsonNode postJson = objectMapper.readTree(postResponse);
		String recordId = postJson.get("data").get("id").asText();

		// 2. 단건 조회 (GET /{runningRecordId})
		mockMvc.perform(get(BASE_URL + "/{id}", recordId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("RUNNING_RECORD_GET_SUCCESS"))
			.andExpect(jsonPath("$.data.id").value(recordId));

		// 3. 전체 기록 조회 (GET, 페이징)
		mockMvc.perform(get(BASE_URL)
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("RUNNING_RECORD_GET_ALL_SUCCESS"))
			.andExpect(jsonPath("$.data.content").isArray())
			.andExpect(jsonPath("$.data.content[0].id").exists());

		// 4. userId 기반 검색 (GET /search)
		mockMvc.perform(get(BASE_URL + "/search")
				.param("userId", userId.toString())
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("RUNNING_RECORD_SEARCH_SUCCESS"))
			.andExpect(jsonPath("$.data.content[0].userId").value(userId.toString()));

		// 5. 기록 삭제 (DELETE)
		mockMvc.perform(delete("/api/v1/app/running-records/{recordId}", recordId)
				.with(user("testUser").roles("MASTER")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("RUNNING_RECORD_DELETE_SUCCESS"))
			.andExpect(jsonPath("$.data.id").value(recordId));

		// 6. 삭제 후 단건 조회 시, 삭제된 레코드는 조회되지 않음 (예외 처리)
		mockMvc.perform(get(BASE_URL + "/{id}", recordId))
			.andExpect(status().isInternalServerError());
	}
}