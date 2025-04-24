package com._42195km.msa.competitionservice.presentation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com._42195km.msa.competitionservice.application.dto.CompleteAppDto;
import com._42195km.msa.competitionservice.application.dto.response.CompetitionAppResponseDto;
import com._42195km.msa.competitionservice.application.facade.CompetitionApplicationFacade;
import com._42195km.msa.competitionservice.domain.model.CompetitionType;
import com._42195km.msa.competitionservice.domain.model.ReceptionType;
import com._42195km.msa.competitionservice.presentation.dto.request.CreateCompetitionRequestDto;
import com._42195km.msa.competitionservice.presentation.dto.request.UpdateCompetitionRequestDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
class CompetitionControllerAPITest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private CompetitionApplicationFacade competitionFacade;

	private UUID testCompetitionId;
	private static final UUID testUserId = UUID.randomUUID();
	private static UUID usedCompetitionId = UUID.randomUUID(); // 실제 테스트에서 사용할 ID
	private static final String TEST_COMPETITION_TITLE = "spring boot 테스트용 대회";
	private CompetitionAppResponseDto testCompetitionDto;

	@BeforeAll
	public void setUp() throws Exception {
		// 테스트용 CreateCompetitionRequestDto 객체 생성
		CreateCompetitionRequestDto createDto = new CreateCompetitionRequestDto();
		// 리플렉션을 사용하여 private 필드에 값 설정
		setFieldValue(createDto, "userId", testUserId);
		setFieldValue(createDto, "title", TEST_COMPETITION_TITLE);
		setFieldValue(createDto, "type", CompetitionType.FULL);
		setFieldValue(createDto, "receptionType", ReceptionType.DRAW);
		setFieldValue(createDto, "participantsNum", 100);
		setFieldValue(createDto, "price", 50000);

		// 대회 생성 API 호출
		mockMvc.perform(post("/api/v1/competitions/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("CPT_001"));

		// 생성한 대회 검색하여 ID 찾기
		MvcResult result = mockMvc.perform(get("/api/v1/competitions/search")
				.param("keyword", TEST_COMPETITION_TITLE)
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andReturn();

		// 응답에서 대회 ID 추출
		String responseContent = result.getResponse().getContentAsString();
		JsonNode rootNode = objectMapper.readTree(responseContent);

		if (rootNode.has("data") && rootNode.get("data").has("content") &&
			rootNode.get("data").get("content").isArray() &&
			rootNode.get("data").get("content").size() > 0) {

			JsonNode competitionNode = rootNode.get("data").get("content").get(0);
			if (competitionNode.has("id")) {
				String id = competitionNode.get("id").asText();
				testCompetitionId = UUID.fromString(id);
				System.out.println("검색을 통해 찾은 테스트 대회 ID: " + testCompetitionId);

				// 조회한 대회 정보 저장
				testCompetitionDto = competitionFacade.getCompetition(testCompetitionId);
			}
		}

		if (testCompetitionId == null) {
			throw new RuntimeException("테스트 대회 ID를 찾지 못했습니다.");
		}

		// 대회 신청 테스트를 위한 대회 생성
		CreateCompetitionRequestDto applicationTestDto = new CreateCompetitionRequestDto();
		setFieldValue(applicationTestDto, "userId", testUserId);
		setFieldValue(applicationTestDto, "title", "신청 테스트용 대회");
		setFieldValue(applicationTestDto, "type", CompetitionType.FULL);
		setFieldValue(applicationTestDto, "receptionType", ReceptionType.FIRST);
		setFieldValue(applicationTestDto, "participantsNum", 100);
		setFieldValue(applicationTestDto, "price", 50000);

		mockMvc.perform(post("/api/v1/competitions/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(applicationTestDto)))
			.andExpect(status().isOk());

		// 생성한 대회 검색하여 ID 찾기 (신청용)
		result = mockMvc.perform(get("/api/v1/competitions/search")
				.param("keyword", "신청 테스트용 대회")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andReturn();

		responseContent = result.getResponse().getContentAsString();
		rootNode = objectMapper.readTree(responseContent);

		if (rootNode.has("data") && rootNode.get("data").has("content") &&
			rootNode.get("data").get("content").isArray() &&
			rootNode.get("data").get("content").size() > 0) {

			JsonNode competitionNode = rootNode.get("data").get("content").get(0);
			if (competitionNode.has("id")) {
				usedCompetitionId = UUID.fromString(competitionNode.get("id").asText());
				System.out.println("신청 테스트용 대회 ID: " + usedCompetitionId);
			}
		}
	}

	@Test
	@Order(2)
	@DisplayName("대회 신청 API 테스트")
	public void testCompleteApplication_Success_2() throws Exception {
		CompleteAppDto requestDto = CompleteAppDto.builder()
			.competitionId(usedCompetitionId)
			.participantId(testUserId)
			.souvenirSelection("T-Shirt")
			.build();

		mockMvc.perform(post("/api/v1/competitions/complete")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("CPT_012"))
			.andExpect(jsonPath("$.message").value("대회 신청 성공"));
	}

	@Test
	@Order(3)
	@DisplayName("대회 신청 API 테스트 - 배송지 입력")
	public void testCompleteApplication_Success_3() throws Exception {
		CompleteAppDto requestDto = CompleteAppDto.builder()
			.competitionId(usedCompetitionId)
			.participantId(testUserId)
			.shippingAddress("서울시 강남구 테헤란로 123")
			.build();

		mockMvc.perform(post("/api/v1/competitions/complete")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("CPT_012"))
			.andExpect(jsonPath("$.message").value("대회 신청 성공"));
	}

	@Test
	@Order(4)
	@DisplayName("대회 신청 API 테스트 - 결제 시작")
	public void testCompleteApplication_Success_4() throws Exception {
		CompleteAppDto requestDto = CompleteAppDto.builder()
			.competitionId(usedCompetitionId)
			.participantId(testUserId)
			.paymentMethod("CARD")
			.build();

		mockMvc.perform(post("/api/v1/competitions/complete")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("CPT_012"))
			.andExpect(jsonPath("$.message").value("대회 신청 성공"));
	}

	@Test
	@Order(5)
	@DisplayName("대회 신청 API 테스트 - 결제 완료")
	public void testCompleteApplication_Success_5() throws Exception {
		CompleteAppDto requestDto = CompleteAppDto.builder()
			.competitionId(usedCompetitionId)
			.participantId(testUserId)
			.paymentStatus("SUCCESS")
			.transactionId("tx-" + System.currentTimeMillis())
			.build();

		mockMvc.perform(post("/api/v1/competitions/complete")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("CPT_012"))
			.andExpect(jsonPath("$.message").value("대회 신청 성공"));
	}

	@Test
	@Order(6)
	@DisplayName("모든 대회 조회 API 테스트")
	public void testGetAllCompetitions() throws Exception {
		// 모든 대회 조회 테스트
		mockMvc.perform(get("/api/v1/competitions/")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("CPT_003"))
			.andExpect(jsonPath("$.message").value("대회 조회 성공"))
			.andExpect(jsonPath("$.data.content").isArray());
		//.andExpect(jsonPath("$.data.content.length()").value(n -> Integer.parseInt(n.toString()) > 0)); // 최소 1개 이상의 항목 존재
	}

	@Test
	@Order(7)
	@DisplayName("특정 대회 조회 API 테스트")
	public void testGetCompetition() throws Exception {
		// 특정 대회 조회 테스트
		mockMvc.perform(get("/api/v1/competitions/{competitionId}", testCompetitionId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("CPT_003"))
			.andExpect(jsonPath("$.message").value("대회 조회 성공"))
			.andExpect(jsonPath("$.data.id").value(testCompetitionId.toString()))
			.andExpect(jsonPath("$.data.title").value(TEST_COMPETITION_TITLE));
	}

	@Test
	@Order(8)
	@DisplayName("대회 검색 API 테스트")
	public void testSearchCompetitions() throws Exception {
		// 대회 검색 테스트
		mockMvc.perform(get("/api/v1/competitions/search")
				.param("keyword", TEST_COMPETITION_TITLE)
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("CPT_006"))
			.andExpect(jsonPath("$.message").value("대회 검색 성공"))
			.andExpect(jsonPath("$.data.content[0].title").value(TEST_COMPETITION_TITLE));
	}

	@Test
	@Order(9)
	@DisplayName("주최 대회 확인 API 테스트")
	public void testCheckCompetition() throws Exception {
		// 주최 대회 확인 테스트
		mockMvc.perform(get("/api/v1/competitions/{userId}/check", testUserId)
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("CPT_003"))
			.andExpect(jsonPath("$.message").value("대회 조회 성공"))
			.andExpect(jsonPath("$.data.content").isArray());
	}

	@Test
	@Order(10)
	@DisplayName("대회 수정 API 테스트")
	public void testUpdateCompetition() throws Exception {
		String updatedTitle = "수정된 " + TEST_COMPETITION_TITLE;

		UpdateCompetitionRequestDto updateDto = new UpdateCompetitionRequestDto();
		setFieldValue(updateDto, "title", updatedTitle);
		setFieldValue(updateDto, "type", CompetitionType.HALF);
		setFieldValue(updateDto, "receptionType", ReceptionType.FIRST);
		setFieldValue(updateDto, "participantsNum", 150);
		setFieldValue(updateDto, "price", 60000);

		mockMvc.perform(patch("/api/v1/competitions/{competitionId}", testCompetitionId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("CPT_008"))
			.andExpect(jsonPath("$.message").value("대회 수정 성공"));

		// 수정 내용 확인
		mockMvc.perform(get("/api/v1/competitions/{competitionId}", testCompetitionId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.title").value(updatedTitle))
			.andExpect(jsonPath("$.data.type").value("HALF"))
			.andExpect(jsonPath("$.data.price").value(60000));
	}

	@Test
	@Order(11)
	@DisplayName("대회 추첨 API 테스트")
	public void testDrawCompetition() throws Exception {
		// 대회 추첨 테스트
		mockMvc.perform(post("/api/v1/competitions/draw/{competitionId}", testCompetitionId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("CPT_018"))
			.andExpect(jsonPath("$.message").value("대회 추첨 성공"));
	}

	@Test
	@Order(12)
	@DisplayName("대회 신청 상태 조회 API 테스트")
	public void testGetApplicationStatus() throws Exception {
		// 상태 조회 테스트
		mockMvc.perform(get("/api/v1/competitions/{competitionId}/{participantId}/status",
				usedCompetitionId, testUserId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("CPT_003"))
			.andExpect(jsonPath("$.message").value("대회 조회 성공"));
	}

	@Test
	@Order(13)
	@DisplayName("대회 삭제 API 테스트")
	public void testDeleteCompetition() throws Exception {
		// 대회 삭제 테스트
		mockMvc.perform(patch("/api/v1/competitions/{competitionId}/delete", testCompetitionId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("CPT_010"))
			.andExpect(jsonPath("$.message").value("대회 삭제 성공"));

		// 삭제 후 조회 시 실패 확인 (삭제된 대회는 조회되지 않아야 함)
		mockMvc.perform(get("/api/v1/competitions/{competitionId}", testCompetitionId))
			.andExpect(status().is4xxClientError());
	}

	// 리플렉션을 사용하여 private 필드에 값을 설정하는 헬퍼 메서드
	private void setFieldValue(Object object, String fieldName, Object value) {
		try {
			java.lang.reflect.Field field = object.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(object, value);
		} catch (Exception e) {
			throw new RuntimeException("필드 설정 중 오류 발생: " + e.getMessage(), e);
		}
	}
}