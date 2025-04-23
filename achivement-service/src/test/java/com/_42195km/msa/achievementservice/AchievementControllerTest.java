package com._42195km.msa.achievementservice;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com._42195km.msa.achievementservice.domain.model.CriteriaInequality;
import com.fasterxml.jackson.databind.ObjectMapper;
import com._42195km.msa.achievementservice.application.dto.request.CreateAchievementCommandDto;
import com._42195km.msa.achievementservice.application.service.AchivementService;
import com._42195km.msa.achievementservice.domain.model.Achievement;
import com._42195km.msa.achievementservice.infrastructure.code.AchievementServiceCode;
import com._42195km.msa.achievementservice.presentation.controller.AchievementController;

import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

@WebMvcTest(controllers = AchievementController.class)
@Import(AchievementControllerTest.MockConfig.class)
@ActiveProfiles("test")
public class AchievementControllerTest {

	@TestConfiguration
	public static class MockConfig {
		@Bean
		public AchivementService achievementService() {
			return Mockito.mock(AchivementService.class);
		}
	}

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;  // JSON 직렬화/역직렬화

	@Autowired
	private AchivementService achievementService;

	@Test
	void testCreateAchievement() throws Exception {
		// JSON 요청 본문 (CreateAchievementRequestDto)
		String jsonRequest = "{" +
			"\"title\": \"New Achievement\"," +
			"\"description\": \"Achievement Description\"," +
			"\"criteria\": \"distance\"," +
			"\"criteriaValue\": 200.0," +
			"\"criteriaType\": \"EQUAL\"" +
			"}";

		// stub: service.createAchievement(...) 호출 시 반환될 Achievement 객체 생성
		Achievement achievement = Achievement.builder()
			.id(UUID.randomUUID())
			.title("New Achievement")
			.description("Achievement Description")
			.criteria("distance")
			.criteriaValue(200.0)
			.criteriaInequality(CriteriaInequality.EQUAL)
			.build();

		when(achievementService.createAchievement(any(CreateAchievementCommandDto.class)))
			.thenReturn(achievement);

		// POST /api/v1/app/achivements 요청 및 응답 검증
		mockMvc.perform(post("/api/v1/app/achievements")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequest))
			.andExpect(status().isOk())
			// ApiResponse 래퍼 내부 필드 검증
			.andExpect(jsonPath("$.code", is(AchievementServiceCode.ACHIEVEMENT_CREATE_SUCCESS.getCode())))
			.andExpect(jsonPath("$.message", is(AchievementServiceCode.ACHIEVEMENT_CREATE_SUCCESS.getMessage())))
			.andExpect(jsonPath("$.status", is(AchievementServiceCode.ACHIEVEMENT_CREATE_SUCCESS.getStatus())))
			.andExpect(jsonPath("$.data.id", notNullValue()))
			.andExpect(jsonPath("$.data.title", is("New Achievement")))
			.andExpect(jsonPath("$.data.description", is("Achievement Description")))
			.andExpect(jsonPath("$.data.criteria", is("distance")))
			.andExpect(jsonPath("$.data.criteriaValue", is(200.0)))
			.andExpect(jsonPath("$.data.criteriaType", is("EQUAL")));
	}

	@Test
	void testGetAchievement() throws Exception {
		UUID achievementId = UUID.randomUUID();
		Achievement achievement = Achievement.builder()
			.id(achievementId)
			.title("Test Achievement")
			.description("Test Description")
			.criteria("distance")
			.criteriaValue(300.0)
			.criteriaInequality(CriteriaInequality.MORE_THAN)
			.build();

		when(achievementService.getAchievementById(achievementId)).thenReturn(achievement);

		mockMvc.perform(get("/api/v1/achievements/{achievementId}", achievementId))
			.andExpect(status().isOk())
			// 반환 데이터는 단일 DTO (GetAchievementResponseDto)로 매핑됨
			.andExpect(jsonPath("$.data.id", is(achievementId.toString())))
			.andExpect(jsonPath("$.data.title", is("Test Achievement")))
			.andExpect(jsonPath("$.data.description", is("Test Description")))
			.andExpect(jsonPath("$.data.criteria", is("distance")))
			.andExpect(jsonPath("$.data.criteriaValue", is(300.0)))
			.andExpect(jsonPath("$.data.criteriaType", is("MORE_THAN")));
	}

	@Test
	void testGetAllAchievements() throws Exception {
		Pageable pageable = PageRequest.of(0, 10);
		Achievement achievement = Achievement.builder()
			.id(UUID.randomUUID())
			.title("All Achievement")
			.description("Description")
			.criteria("distance")
			.criteriaValue(150.0)
			.criteriaInequality(CriteriaInequality.EQUAL)
			.build();
		PageImpl<Achievement> page = new PageImpl<>(Collections.singletonList(achievement), pageable, 1);

		when(achievementService.getAchievements(any(Pageable.class))).thenReturn(page);

		mockMvc.perform(get("/api/v1/achievements")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code", is(AchievementServiceCode.ACHIEVEMENT_GET_ALL_SUCCESS.getCode())))
			.andExpect(jsonPath("$.data.content", hasSize(1)))
			.andExpect(jsonPath("$.data.content[0].title", is("All Achievement")));
	}

	@Test
	void testSearchAchievements() throws Exception {
		String keyword = "Champion";
		Pageable pageable = PageRequest.of(0, 10);
		Achievement achievement = Achievement .builder()
			.id(UUID.randomUUID())
			.title("Sprint Champion")
			.description("Winner in sprint")
			.criteria("pace")
			.criteriaValue(9.5)
			.criteriaInequality(CriteriaInequality.MORE_THAN)
			.build();
		PageImpl<Achievement> page = new PageImpl<>(Collections.singletonList(achievement), pageable, 1);

		when(achievementService.searchAchievements(keyword, pageable)).thenReturn(page);

		mockMvc.perform(get("/api/v1/achievements/search")
				.param("title", keyword)
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code", is(AchievementServiceCode.ACHIEVEMENT_SEARCH_SUCCESS.getCode())))
			.andExpect(jsonPath("$.data.content", hasSize(1)))
			.andExpect(jsonPath("$.data.content[0].title", is("Sprint Champion")));
	}

	@Test
	void testGetAchievementsByUser() throws Exception {
		UUID userId = UUID.randomUUID();
		Pageable pageable = PageRequest.of(0, 10);
		Achievement achievement = Achievement.builder()
			.id(UUID.randomUUID())
			.title("User Achievement")
			.description("User's achievement")
			.criteria("pace")
			.criteriaValue(250.0)
			.criteriaInequality(CriteriaInequality.LESS_THAN)
			.build();
		PageImpl<Achievement> page = new PageImpl<>(Collections.singletonList(achievement), pageable, 1);

		when(achievementService.getAchivementsByUser(userId, pageable)).thenReturn(page);

		mockMvc.perform(get("/api/v1/achievements/user/{userId}", userId)
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code", is(AchievementServiceCode.ACHIEVEMENT_GET_BY_USER_SUCCESS.getCode())))
			.andExpect(jsonPath("$.data.content", hasSize(1)))
			.andExpect(jsonPath("$.data.content[0].title", is("User Achievement")));
	}

	@Test
	void testDeleteAchievement() throws Exception {
		UUID achievementId = UUID.randomUUID();
		Achievement achievement = Achievement.builder()
			.id(achievementId)
			.title("To be deleted")
			.description("Delete this achievement")
			.criteria("pace")
			.criteriaValue(180.0)
			.criteriaInequality(CriteriaInequality.EQUAL)
			.build();

		when(achievementService.deleteAchievement(achievementId)).thenReturn(achievement);

		mockMvc.perform(delete("/api/v1/achievements/{achievementId}", achievementId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.id", is(achievementId.toString())))
			.andExpect(jsonPath("$.data.title", is("To be deleted")))
			.andExpect(jsonPath("$.data.description", is("Delete this achievement")))
			.andExpect(jsonPath("$.data.criteria", is("pace")))
			.andExpect(jsonPath("$.data.criteriaValue", is(180.0)))
			.andExpect(jsonPath("$.data.criteriaType", is("EQUAL")));
	}
}