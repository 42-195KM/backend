package com._42195km.msa.competitionservice.presentation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com._42195km.msa.competitionservice.application.dto.CompleteAppDto;
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
@EnableAutoConfiguration(exclude = {
	KafkaAutoConfiguration.class,
	RedisAutoConfiguration.class,
	EurekaClientAutoConfiguration.class
})
class CompetitionControllerAPITest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private static UUID testCompetitionId;
	private static UUID testUserId = UUID.randomUUID();
	private final UUID usedCompetitionId = UUID.fromString("872dd912-8616-4595-aa94-5737bf633012");
	private final String TEST_COMPETITION_TITLE = "spring boot 테스트용 대회";

	@Test
	@Order(1)
	@DisplayName("대회 생성 API 테스트 - 수정/삭제 테스트용")
	public void testCreateCompetitionForUpdateDelete() throws Exception {
		// 테스트용 CreateCompetitionRequestDto 객체 생성
		CreateCompetitionRequestDto createDto = new CreateCompetitionRequestDto();
		// 리플렉션을 사용하여 private 필드에 값 설정
		setFieldValue(createDto, "userId", testUserId);
		setFieldValue(createDto, "title", TEST_COMPETITION_TITLE);
		setFieldValue(createDto, "type", CompetitionType.FULL);
		setFieldValue(createDto, "receptionType", ReceptionType.DRAW);
		setFieldValue(createDto, "participantsNum", 100);
		setFieldValue(createDto, "price", 50000);

		mockMvc.perform(post("/api/v1/competitions/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("CPT_001"))
			.andExpect(jsonPath("$.message").value("대회 생성 성공"));
	}

	@Test
	@Order(2)
	@DisplayName("생성한 대회 검색하여 ID 찾기")
	public void testSearchCreatedCompetition() throws Exception {
		// 생성한 대회 검색
		MvcResult result = mockMvc.perform(get("/api/v1/competitions/search")
				.param("keyword", TEST_COMPETITION_TITLE)
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("CPT_006"))
			.andExpect(jsonPath("$.message").value("대회 검색 성공"))
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
			}
		}

		if (testCompetitionId == null) {
			System.out.println("경고: 테스트 대회 ID를 찾지 못했습니다. 기본 ID를 사용합니다.");
		}
	}

	@Test
	@Order(3)
	@DisplayName("대회 신청 API 테스트 - 약관 동의")
	public void testCompleteApplication_Success_1() throws Exception {

		CompleteAppDto requestDto = CompleteAppDto.builder()
			.competitionId(usedCompetitionId)
			.participantId(testUserId)
			.termsAgreed(true)
			.souvenirSelection("T-Shirt")
			.shippingAddress("123 Example Street")
			.paymentMethod("CreditCard")
			.paymentStatus("SUCCESS")
			.transactionId("tx-001")
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
	@DisplayName("대회 신청 API 테스트 - 기념품 선택")
	public void testCompleteApplication_Success_2() throws Exception {

		CompleteAppDto requestDto = CompleteAppDto.builder()
			.competitionId(usedCompetitionId)
			.participantId(testUserId)
			.termsAgreed(true)
			.souvenirSelection("T-Shirt")
			.shippingAddress("123 Example Street")
			.paymentMethod("CreditCard")
			.paymentStatus("SUCCESS")
			.transactionId("tx-001")
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
	@DisplayName("대회 신청 API 테스트 - 배송지 입력")
	public void testCompleteApplication_Success_3() throws Exception {

		CompleteAppDto requestDto = CompleteAppDto.builder()
			.competitionId(usedCompetitionId)
			.participantId(testUserId)
			.termsAgreed(true)
			.souvenirSelection("T-Shirt")
			.shippingAddress("123 Example Street")
			.paymentMethod("CreditCard")
			.paymentStatus("SUCCESS")
			.transactionId("tx-001")
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
	@DisplayName("대회 신청 API 테스트 - 결제 시작")
	public void testCompleteApplication_Success_4() throws Exception {
		CompleteAppDto requestDto = CompleteAppDto.builder()
			.competitionId(usedCompetitionId)
			.participantId(testUserId)
			.termsAgreed(true)
			.souvenirSelection("T-Shirt")
			.shippingAddress("123 Example Street")
			.paymentMethod("CreditCard")
			.paymentStatus("SUCCESS")
			.transactionId("tx-001")
			.build();

		mockMvc.perform(post("/api/v1/competitions/complete")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("CPT_012"))
			.andExpect(jsonPath("$.message").value("대회 신청 성공"));
	}

	@Test
	@Order(7)
	@DisplayName("대회 신청 API 테스트 - 결제 완료")
	public void testCompleteApplication_Success_5() throws Exception {
		CompleteAppDto requestDto = CompleteAppDto.builder()
			.competitionId(usedCompetitionId)
			.participantId(testUserId)
			.termsAgreed(true)
			.souvenirSelection("T-Shirt")
			.shippingAddress("123 Example Street")
			.paymentMethod("CreditCard")
			.paymentStatus("SUCCESS")
			.transactionId("tx-001")
			.build();

		mockMvc.perform(post("/api/v1/competitions/complete")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("CPT_012"))
			.andExpect(jsonPath("$.message").value("대회 신청 성공"));
	}

	@Test
	@Order(8)
	@DisplayName("모든 대회 조회 API 테스트")
	public void testGetAllCompetitions() throws Exception {
		// 모든 대회 조회 테스트
		mockMvc.perform(get("/api/v1/competitions/")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("CPT_003"))
			.andExpect(jsonPath("$.message").value("대회 조회 성공"));
	}

	@Test
	@Order(9)
	@DisplayName("특정 대회 조회 API 테스트")
	public void testGetCompetition() throws Exception {
		// 특정 대회 조회 테스트
		mockMvc.perform(get("/api/v1/competitions/{competitionId}", usedCompetitionId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("CPT_003"))
			.andExpect(jsonPath("$.message").value("대회 조회 성공"));
	}

	@Test
	@DisplayName("대회 검색 API 테스트")
	public void testSearchCompetitions() throws Exception {
		// 대회 검색 테스트
		mockMvc.perform(get("/api/v1/competitions/search")
				.param("keyword", "마라톤")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("CPT_006"))
			.andExpect(jsonPath("$.message").value("대회 검색 성공"));
	}

	@Test
	@DisplayName("주최 대회 확인 API 테스트")
	public void testCheckCompetition() throws Exception {
		// 주최 대회 확인 테스트
		mockMvc.perform(get("/api/v1/competitions/{userId}/check", testUserId)
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("CPT_003"))
			.andExpect(jsonPath("$.message").value("대회 조회 성공"));
	}

	@Test
	@DisplayName("대회 수정 API 테스트")
	public void testUpdateCompetition() throws Exception {
		UpdateCompetitionRequestDto updateDto = new UpdateCompetitionRequestDto();
		setFieldValue(updateDto, "title", "수정된 " + TEST_COMPETITION_TITLE);
		setFieldValue(updateDto, "type", CompetitionType.HALF);
		setFieldValue(updateDto, "receptionType", ReceptionType.FIRST);
		setFieldValue(updateDto, "participantsNum", 150);
		setFieldValue(updateDto, "price", 60000);

		mockMvc.perform(put("/api/v1/competitions/{competitionId}", testCompetitionId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("CPT_008"))
			.andExpect(jsonPath("$.message").value("대회 수정 성공"));
	}

	@Test
	@Order(12)
	@DisplayName("대회 삭제 API 테스트")
	public void testDeleteCompetition() throws Exception {
		// 대회 삭제 테스트
		mockMvc.perform(patch("/api/v1/competitions/{competitionId}/delete", testCompetitionId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("CPT_010"))
			.andExpect(jsonPath("$.message").value("대회 삭제 성공"));
	}

	@Test
	@Order(10)
	@DisplayName("대회 추첨 API 테스트")
	public void testDrawCompetition() throws Exception {
		// 대회 추첨 테스트
		mockMvc.perform(post("/api/v1/competitions/draw/{competitionId}", testCompetitionId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("CPT_018"))
			.andExpect(jsonPath("$.message").value("대회 추첨 성공"));
	}

	@Test
	@Order(11)
	@DisplayName("대회 신청 상태 조회 API 테스트")
	public void testGetApplicationStatus() throws Exception {
		// 상태 조회
		mockMvc.perform(get("/api/v1/competitions/{competitionId}/{participantId}/status",
				testCompetitionId, testUserId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("CPT_003"))
			.andExpect(jsonPath("$.message").value("대회 조회 성공"));
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