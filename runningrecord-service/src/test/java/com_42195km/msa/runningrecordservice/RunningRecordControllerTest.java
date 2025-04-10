package com_42195km.msa.runningrecordservice;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com_42195km.msa.runningrecordservice.application.dto.request.CreateRunningRecordCommandDto;
import com_42195km.msa.runningrecordservice.application.service.RunningRecordService;
import com_42195km.msa.runningrecordservice.domain.model.RunningRecord;
import com_42195km.msa.runningrecordservice.infrastructure.config.RunningRecordServiceCode;
import com_42195km.msa.runningrecordservice.presentation.controller.RunningRecordController;
import java.sql.Timestamp;
import java.time.Duration;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

@WebMvcTest(controllers = RunningRecordController.class)
@Import(RunningRecordControllerTest.MockConfig.class)
public class RunningRecordControllerTest {

	@TestConfiguration
	public static class MockConfig {
		@Bean
		public RunningRecordService runningRecordService() {
			return Mockito.mock(RunningRecordService.class);
		}
	}

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private RunningRecordService runningRecordService;

	@Test
	void testCreateRunningRecord() throws Exception {
		// 테스트용 Duration 값을 생성 (예: 100초)
		Duration testDuration = Duration.ofSeconds(100);

		// 입력 JSON (CreateRunningRecordRequestDto)
		String jsonRequest = "{" +
			"\"userId\": \"" + UUID.randomUUID() + "\"," +
			"\"distance\": 5.5," +
			"\"timer\": \"" + testDuration.toString() + "\"," +
			"\"pace\": 6.2" +
			"}";

		// stub: 생성 시 호출될 service.createRunningRecord(...) -> 반환할 RunningRecord
		RunningRecord record = RunningRecord.builder()
			.id(UUID.randomUUID())
			.userId(UUID.randomUUID())
			.distance(5.5)
			.timer(testDuration)
			.pace(6.2)
			.build();

		when(runningRecordService.createRunningRecord(any(CreateRunningRecordCommandDto.class)))
			.thenReturn(record);

		mockMvc.perform(post("/api/v1/running-records")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequest))
			.andExpect(status().isOk())
			// ApiResponse 래핑 내부 필드 검증
			.andExpect(jsonPath("$.code", is(RunningRecordServiceCode.RUNNING_RECORD_CREATE_SUCCESS.getCode())))
			.andExpect(jsonPath("$.message", is(RunningRecordServiceCode.RUNNING_RECORD_CREATE_SUCCESS.getMessage())))
			.andExpect(jsonPath("$.status", is(RunningRecordServiceCode.RUNNING_RECORD_CREATE_SUCCESS.getStatus())))
			.andExpect(jsonPath("$.data.id", notNullValue()))
			.andExpect(jsonPath("$.data.distance", is(5.5)))
			.andExpect(jsonPath("$.data.pace", is(6.2)));
	}

	@Test
	void testGetRunningRecord() throws Exception {
		UUID recordId = UUID.randomUUID();
		Duration testDuration = Duration.ofSeconds(120);
		RunningRecord record = RunningRecord.builder()
			.id(recordId)
			.userId(UUID.randomUUID())
			.distance(8.0)
			.timer(testDuration)
			.pace(6.0)
			.build();

		when(runningRecordService.getRecordById(recordId)).thenReturn(record);

		mockMvc.perform(get("/api/v1/running-records/{runningRecordId}", recordId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.id", is(recordId.toString())))
			.andExpect(jsonPath("$.data.distance", is(8.0)))
			.andExpect(jsonPath("$.data.pace", is(6.0)));
	}

	@Test
	void testGetAllRunningRecords() throws Exception {
		Duration testDuration = Duration.ofSeconds(90);
		Pageable pageable = PageRequest.of(0, 10);
		RunningRecord record = RunningRecord.builder()
			.id(UUID.randomUUID())
			.userId(UUID.randomUUID())
			.distance(7.0)
			.timer(testDuration)
			.pace(5.5)
			.build();
		PageImpl<RunningRecord> page = new PageImpl<>(Collections.singletonList(record), pageable, 1);

		when(runningRecordService.getAllRecords(any(Pageable.class))).thenReturn(page);

		mockMvc.perform(get("/api/v1/running-records")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code", is(RunningRecordServiceCode.RUNNING_RECORD_GET_ALL_SUCCESS.getCode())))
			.andExpect(jsonPath("$.data.content", hasSize(1)))
			.andExpect(jsonPath("$.data.content[0].distance", is(7.0)));
	}

	@Test
	void testSearchRunningRecords() throws Exception {
		UUID userId = UUID.randomUUID();
		Duration testDuration = Duration.ofSeconds(150);
		Pageable pageable = PageRequest.of(0, 10);
		RunningRecord record = RunningRecord.builder()
			.id(UUID.randomUUID())
			.userId(userId)
			.distance(9.0)
			.timer(testDuration)
			.pace(6.8)
			.build();
		PageImpl<RunningRecord> page = new PageImpl<>(Collections.singletonList(record), pageable, 1);

		when(runningRecordService.searchRecords(userId, pageable)).thenReturn(page);

		mockMvc.perform(get("/api/v1/running-records/search")
				.param("userId", userId.toString())
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code", is(RunningRecordServiceCode.RUNNING_RECORD_SEARCH_SUCCESS.getCode())))
			.andExpect(jsonPath("$.data.content", hasSize(1)))
			.andExpect(jsonPath("$.data.content[0].userId", is(userId.toString())));
	}

	@Test
	void testDeleteRunningRecord() throws Exception {
		Duration testDuration = Duration.ofSeconds(180);
		UUID runningRecordId = UUID.randomUUID();
		RunningRecord record = RunningRecord.builder()
			.id(runningRecordId)
			.userId(UUID.randomUUID())
			.distance(10.0)
			.timer(testDuration)
			.pace(7.2)
			.build();

		when(runningRecordService.deleteRecord(runningRecordId)).thenReturn(record);

		mockMvc.perform(delete("/api/v1/running-records/{runningRecordId}", runningRecordId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.id", is(runningRecordId.toString())))
			.andExpect(jsonPath("$.data.distance", is(10.0)))
			.andExpect(jsonPath("$.data.pace", is(7.2)));
	}
}
