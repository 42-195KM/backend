package com._42195km.msa.runningrecordservice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com._42195km.msa.runningrecordservice.application.dto.request.CreateRunningRecordCommandDto;
import com._42195km.msa.runningrecordservice.application.service.RunningRecordServiceImpl;
import com._42195km.msa.runningrecordservice.domain.model.RunningRecord;
import com._42195km.msa.runningrecordservice.domain.repository.RunningRecordRepository;
import com._42195km.msa.common.exception.CustomBusinessException;

@ExtendWith(MockitoExtension.class)
class RunningRecordServiceImplTest {

	@InjectMocks
	private RunningRecordServiceImpl runningRecordService;

	@Mock
	private RunningRecordRepository runningRecordRepository;

	@Test
	void testCreateRunningRecordSuccess() {
		// Given
		UUID userId = UUID.randomUUID();
		double distance = 5.0;
		Duration timer = Duration.ofSeconds(100);
		double pace = 6.0;
		CreateRunningRecordCommandDto dto = new CreateRunningRecordCommandDto(userId, distance, timer, pace);

		// createRunningRecord 메서드를 통해 생성된 RunningRecord 객체 (id는 repository.save에 의해 할당될 것이라 가정)
		RunningRecord runningRecord = RunningRecord.createRunningRecord(dto);
		// 테스트를 위해 임의의 id 할당
		runningRecord.setId(UUID.randomUUID());

		when(runningRecordRepository.save(any(RunningRecord.class))).thenReturn(runningRecord);

		// When
		RunningRecord result = runningRecordService.createRunningRecord(dto);

		// Then
		assertNotNull(result);
		assertEquals(runningRecord.getId(), result.getId());
		verify(runningRecordRepository, times(1)).save(any(RunningRecord.class));
	}

	@Test
	void testCreateRunningRecordFailure() {
		// Given
		UUID userId = UUID.randomUUID();
		double distance = 5.0;
		Duration timer = Duration.ofSeconds(50);
		double pace = 6.0;
		CreateRunningRecordCommandDto dto = new CreateRunningRecordCommandDto(userId, distance, timer, pace);

		// repository.save() 호출 시 예외 발생 시뮬레이션
		when(runningRecordRepository.save(any(RunningRecord.class)))
			.thenThrow(new RuntimeException("DB error"));

		// When & Then: CustomBusinessException이 발생해야 함
		// getMessage() 등을 사용해 메시지를 추가로 검증할 수 있음 (필요시)
		assertThrows(CustomBusinessException.class, () -> {
			runningRecordService.createRunningRecord(dto);
		});
	}

	@Test
	void testGetRecordByIdSuccess() {
		// Given
		UUID recordId = UUID.randomUUID();
		RunningRecord runningRecord = new RunningRecord();
		runningRecord.setId(recordId);
		runningRecord.setUserId(UUID.randomUUID());
		runningRecord.setDistance(10.0);
		runningRecord.setTimer(Duration.ofSeconds(120));
		runningRecord.setPace(5.5);

		when(runningRecordRepository.findById(recordId))
			.thenReturn(Optional.of(runningRecord));

		// When
		RunningRecord result = runningRecordService.getRecordById(recordId);

		// Then
		assertNotNull(result);
		assertEquals(recordId, result.getId());
		verify(runningRecordRepository, times(1)).findById(recordId);
	}

	@Test
	void testGetRecordByIdNotFound() {
		// Given
		UUID recordId = UUID.randomUUID();
		when(runningRecordRepository.findById(recordId))
			.thenReturn(Optional.empty());

		// When & Then: 존재하지 않으므로 CustomBusinessException 발생 확인
		assertThrows(CustomBusinessException.class, () -> {
			runningRecordService.getRecordById(recordId);
		});
	}

	@Test
	void testGetAllRecordsSuccess() {
		// Given
		Pageable pageable = PageRequest.of(0, 10);

		RunningRecord record1 = new RunningRecord();
		record1.setId(UUID.randomUUID());
		record1.setUserId(UUID.randomUUID());
		record1.setDistance(5.0);
		record1.setTimer(Duration.ofSeconds(120));
		record1.setPace(6.0);

		RunningRecord record2 = new RunningRecord();
		record2.setId(UUID.randomUUID());
		record2.setUserId(UUID.randomUUID());
		record2.setDistance(10.0);
		record2.setTimer(Duration.ofSeconds(130));
		record2.setPace(5.5);

		Page<RunningRecord> page = new PageImpl<>(List.of(record1, record2));
		when(runningRecordRepository.findAll(pageable)).thenReturn(page);

		// When
		Page<RunningRecord> result = runningRecordService.getAllRecords(pageable);

		// Then
		assertNotNull(result);
		assertEquals(2, result.getContent().size());
		verify(runningRecordRepository, times(1)).findAll(pageable);
	}

	@Test
	void testSearchRecordsSuccess() {
		// Given
		UUID userId = UUID.randomUUID();
		Pageable pageable = PageRequest.of(0, 10);

		RunningRecord record = new RunningRecord();
		record.setId(UUID.randomUUID());
		record.setUserId(userId);
		record.setDistance(7.0);
		record.setTimer(Duration.ofSeconds(190));
		record.setPace(6.5);

		Page<RunningRecord> page = new PageImpl<>(List.of(record));
		LocalDateTime searchFromDate = LocalDateTime.of(1970, 1, 1, 0, 0);
		when(runningRecordRepository.searchByUserId(userId, searchFromDate, pageable))
			.thenReturn(page);

		// When
		Page<RunningRecord> result = runningRecordService.searchRecords(userId, searchFromDate, pageable);

		// Then
		assertNotNull(result);
		assertEquals(1, result.getContent().size());
		verify(runningRecordRepository, times(1)).searchByUserId(userId, searchFromDate, pageable);
	}

	@Test
	void testDeleteRecordSuccess() {
		// Given
		UUID recordId = UUID.randomUUID();
		RunningRecord runningRecord = new RunningRecord();
		runningRecord.setId(recordId);
		runningRecord.setUserId(UUID.randomUUID());
		runningRecord.setDistance(15.0);
		runningRecord.setTimer(Duration.ofSeconds(180));
		runningRecord.setPace(7.0);

		when(runningRecordRepository.findById(recordId))
			.thenReturn(Optional.of(runningRecord));

		// When
		RunningRecord result = runningRecordService.deleteRecord(recordId);

		// Then
		assertNotNull(result);
		assertEquals(recordId, result.getId());
		verify(runningRecordRepository, times(1)).findById(recordId);
	}

	@Test
	void testDeleteRecordNotFound() {
		// Given
		UUID recordId = UUID.randomUUID();
		when(runningRecordRepository.findById(recordId))
			.thenReturn(Optional.empty());

		// When & Then
		assertThrows(CustomBusinessException.class, () -> {
			runningRecordService.deleteRecord(recordId);
		});
	}
}
