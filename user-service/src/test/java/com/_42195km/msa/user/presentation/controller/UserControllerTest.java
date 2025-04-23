package com._42195km.msa.user.presentation.controller;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com._42195km.msa.user.application.dto.request.CreateUserRequestDto;
import com._42195km.msa.user.application.dto.request.UpdateUserRequestDto;
import com._42195km.msa.user.application.dto.response.CreateUserResponseDto;
import com._42195km.msa.user.application.dto.response.GetAllUserResponseDto;
import com._42195km.msa.user.application.dto.response.GetUserResponseDto;
import com._42195km.msa.user.application.dto.response.UpdateUserResponseDto;
import com._42195km.msa.user.application.service.UserServiceImpl;
import com._42195km.msa.user.domain.model.Gender;
import com._42195km.msa.user.domain.model.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

// 컨트롤러 테스트
@WebMvcTest(controllers = UserController.class)
@ActiveProfiles("test")
class UserControllerTest {

	private static final Logger log = LoggerFactory.getLogger(UserControllerTest.class);

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private UserServiceImpl userServiceImpl;

	@Test
	@DisplayName("[User-Service] >> 유저 생성 API 테스트")
	void testCreateUser() throws Exception {

		String createUserJson = "{\n"
			+ "    \"username\":\"TestMaster\",\n"
			+ "    \"password\":\"TestMaster\",\n"
			+ "    \"email\":\"testmail@test.test\",\n"
			+ "    \"birth\":\"1999-01-01\",\n"
			+ "    \"gender\":\"M\",\n"
			+ "    \"role\":\"MASTER\",\n"
			+ "    \"mediaId\":\"U9999999\",\n"
			+ "    \"phone\":\"010-9999-9999\"\n"
			+ "}";

		CreateUserResponseDto fakeResponseDto = CreateUserResponseDto
			.builder()
			.id(UUID.randomUUID())
			.username("TestMaster")
			.email("testmail@test.test")
			.birth(Date.valueOf("1999-01-01"))
			.gender(Gender.M)
			.role(UserRole.MASTER)
			.mediaId("U9999999")
			.phone("010-9999-9999")
			.build();

		when(userServiceImpl.createUser(any(CreateUserRequestDto.class)))
			.thenReturn(fakeResponseDto);

		String testUserId = String.valueOf(fakeResponseDto.getId());

		mvc.perform(post("/api/v1/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(createUserJson))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.code").value("S_USER_CREATED"))
			.andExpect(jsonPath("$.data").exists())
			.andDo(
				result -> {
					String responseBody = result.getResponse().getContentAsString();

					assertThat(JsonPath.read(responseBody, "$.data.id").toString()).isEqualTo(testUserId);
					assertThat(JsonPath.read(responseBody, "$.data.username").toString()).isEqualTo("TestMaster");
					assertThat(JsonPath.read(responseBody, "$.data.email").toString()).isEqualTo("testmail@test.test");
					assertThat(JsonPath.read(responseBody, "$.data.birth").toString()).isEqualTo("1999-01-01");
					assertThat(JsonPath.read(responseBody, "$.data.gender").toString()).isEqualTo("M");
					assertThat(JsonPath.read(responseBody, "$.data.role").toString()).isEqualTo("MASTER");
					assertThat(JsonPath.read(responseBody, "$.data.mediaId").toString()).isEqualTo("U9999999");
					assertThat(JsonPath.read(responseBody, "$.data.phone").toString()).isEqualTo("010-9999-9999");
				}
			);
	}

	@Test
	@DisplayName("[User-Service] >> 유저 단건 조회")
	void testGetUser() throws Exception {

		GetUserResponseDto fakeResponseDto = GetUserResponseDto
			.builder()
			.id(UUID.randomUUID())
			.username("TestMaster")
			.email("testmail@test.test")
			.birth(Date.valueOf("1999-01-01"))
			.gender(Gender.M)
			.role(UserRole.MASTER)
			.mediaId("U9999999")
			.phone("010-9999-9999")
			.build();

		when(userServiceImpl.getUser(any(UUID.class)))
			.thenReturn(fakeResponseDto);

		String testUserId = String.valueOf(fakeResponseDto.getId());

		// 단건 조회 API 호출
		mvc.perform(get("/api/v1/app/users/{userId}", testUserId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("S_USER_FIND_ONE"))
			.andExpect(jsonPath("$.data").exists())
			.andDo(
				result -> {
					String responseBody = result.getResponse().getContentAsString();

					assertThat(JsonPath.read(responseBody, "$.data.id").toString()).isEqualTo(testUserId);
					assertThat(JsonPath.read(responseBody, "$.data.username").toString()).isEqualTo("TestMaster");
					assertThat(JsonPath.read(responseBody, "$.data.email").toString()).isEqualTo("testmail@test.test");
					assertThat(JsonPath.read(responseBody, "$.data.birth").toString()).isEqualTo("1999-01-01");
					assertThat(JsonPath.read(responseBody, "$.data.gender").toString()).isEqualTo("M");
					assertThat(JsonPath.read(responseBody, "$.data.role").toString()).isEqualTo("MASTER");
					assertThat(JsonPath.read(responseBody, "$.data.mediaId").toString()).isEqualTo("U9999999");
					assertThat(JsonPath.read(responseBody, "$.data.phone").toString()).isEqualTo("010-9999-9999");
				}
			);
	}

	@Test
	@DisplayName("[User-Serivce] >> 유저 모든 목록 조회")
	void testGetAllUsers() throws Exception {

		GetAllUserResponseDto fakeResponseDto1 = GetAllUserResponseDto
			.builder()
			.id(UUID.randomUUID())
			.username("TestMaster1")
			.email("testmail1@test.test")
			.birth(Date.valueOf("1999-01-01"))
			.gender(Gender.M)
			.role(UserRole.MASTER)
			.mediaId("U9999991")
			.phone("010-9999-9991")
			.build();

		GetAllUserResponseDto fakeResponseDto2 = GetAllUserResponseDto
			.builder()
			.id(UUID.randomUUID())
			.username("TestMaster2")
			.email("testmail2@test.test")
			.birth(Date.valueOf("1998-01-01"))
			.gender(Gender.F)
			.role(UserRole.NORMAL)
			.mediaId("U9999992")
			.phone("010-9999-9992")
			.build();

		Page<GetAllUserResponseDto> fakePage = new PageImpl<>(List.of(fakeResponseDto1, fakeResponseDto2),
			PageRequest.of(0, 10), 20);

		when(userServiceImpl.getAllUsers(any(Pageable.class))).thenReturn(fakePage);

		mvc.perform(get("/api/v1/app/users"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("S_USER_FIND_ALL"))
			.andExpect(jsonPath("$.data.content.length()").value(fakePage.getContent().size()));
	}

	@Test
	@DisplayName("[User-Service] >> 유저 정보 수정")
	void updateUser() throws Exception {

		String updateUserJson = "{\n"
			+ "    \"username\":\"UpdatedName\",\n"
			+ "    \"email\":\"updated@test.test\",\n"
			+ "    \"phone\":\"010-8888-8888\"\n"
			+ "}";

		UpdateUserResponseDto updatedUser = UpdateUserResponseDto.builder()
			.id(UUID.randomUUID())
			.username("UpdatedName")
			.email("updated@test.test")
			.phone("010-8888-8888")
			.build();

		UUID userId = UUID.randomUUID();

		// when
		when(userServiceImpl.updateUser(eq(userId), any(UpdateUserRequestDto.class)))
			.thenReturn(updatedUser);

		// then
		mvc.perform(patch("/api/v1/app/users/{userId}", userId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(updateUserJson))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("S_USER_UPDATE"))
			.andExpect(jsonPath("$.data.username").value("UpdatedName"))
			.andExpect(jsonPath("$.data.email").value("updated@test.test"))
			.andExpect(jsonPath("$.data.phone").value("010-8888-8888"));
	}

	@Test
	@DisplayName("[User-Service] >> 회원 탈퇴")
	void deleteUser() throws Exception {

		UUID userId = UUID.randomUUID();

		doNothing().when(userServiceImpl).deleteUser(eq(userId));

		mvc.perform(delete("/api/v1/app/users/{userId}", userId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("S_USER_DELETE_SUCCESS"));
	}

	@Test
	@DisplayName("[User-Service] >> 밴")
	void banUser() throws Exception {

		UUID userId = UUID.randomUUID();

		doNothing().when(userServiceImpl).banUser(eq(userId));

		mvc.perform(delete("/api/v1/app/users/ban/{userId}", userId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("S_USER_BAN_USER_SUCCESS"));
	}
}