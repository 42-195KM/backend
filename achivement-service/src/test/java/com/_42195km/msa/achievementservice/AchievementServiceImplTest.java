package com._42195km.msa.achievementservice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com._42195km.msa.achievementservice.application.dto.request.CreateAchievementCommandDto;
import com._42195km.msa.achievementservice.application.service.AchievementServiceImpl;
import com._42195km.msa.achievementservice.domain.model.Achievement;
import com._42195km.msa.achievementservice.domain.repository.AchievementRepository;
import com._42195km.msa.achievementservice.domain.repository.AchievementUserRepository;
import com._42195km.msa.common.exception.CustomBusinessException;
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

@ExtendWith(MockitoExtension.class)
public class AchievementServiceImplTest {

	@InjectMocks
	private AchievementServiceImpl achievementService;

	@Mock
	private AchievementRepository achievementRepository;

	@Mock
	private AchievementUserRepository achievementUserRepository;

	@Test
	void testCreateAchievementSuccess() {
		// given: CreateAchievementCommandDto로 업적 생성
		CreateAchievementCommandDto commandDto = new CreateAchievementCommandDto(
			"Test Title",
			"Test Description",
			"criteria",
			100.0,
			"EQUAL"
		);

		// 도메인 모델 생성 (내부에서 dto의 값들이 매핑됨)
		Achievement achievement = Achievement.createAchievement(commandDto);
		achievement.setId(UUID.randomUUID());

		// achievementRepository.save()가 호출될 때 achievement 반환
		when(achievementRepository.save(any(Achievement.class))).thenReturn(achievement);

		// when: 서비스 메서드 호출
		Achievement result = achievementService.createAchievement(commandDto);

		// then: 결과가 null이 아니며 ID가 일치하는지 확인
		assertNotNull(result);
		assertEquals(achievement.getId(), result.getId());
		verify(achievementRepository).save(any(Achievement.class));
	}

	@Test
	void testCreateAchievementFailure() {
		// given
		CreateAchievementCommandDto commandDto = new CreateAchievementCommandDto(
			"Test Title",
			"Test Description",
			"criteria",
			100.0,
			"EQUAL"
		);

		// repository.save() 호출 시 예외 발생 시뮬레이션
		when(achievementRepository.save(any(Achievement.class)))
			.thenThrow(new RuntimeException("DB error"));

		// when & then: CustomBusinessException 발생 검증
		assertThrows(CustomBusinessException.class, () -> {
			achievementService.createAchievement(commandDto);
		});
	}

	@Test
	void testGetAchievementByIdSuccess() {
		// given
		UUID achievementId = UUID.randomUUID();
		Achievement achievement = new Achievement();
		achievement.setId(achievementId);
		achievement.setTitle("Sample Achievement");

		when(achievementRepository.findById(achievementId)).thenReturn(Optional.of(achievement));

		// when
		Achievement result = achievementService.getAchievementById(achievementId);

		// then
		assertNotNull(result);
		assertEquals("Sample Achievement", result.getTitle());
		verify(achievementRepository).findById(achievementId);
	}

	@Test
	void testGetAchievementByIdNotFound() {
		// given
		UUID achievementId = UUID.randomUUID();
		when(achievementRepository.findById(achievementId)).thenReturn(Optional.empty());

		// when & then
		assertThrows(CustomBusinessException.class, () -> {
			achievementService.getAchievementById(achievementId);
		});
	}

	@Test
	void testGetAchievements() {
		// given: 페이징된 업적 목록 생성
		Pageable pageable = PageRequest.of(0, 10);
		Achievement achievement1 = new Achievement();
		achievement1.setId(UUID.randomUUID());
		achievement1.setTitle("Achievement 1");

		Achievement achievement2 = new Achievement();
		achievement2.setId(UUID.randomUUID());
		achievement2.setTitle("Achievement 2");

		Page<Achievement> page = new PageImpl<>(java.util.List.of(achievement1, achievement2), pageable, 2);
		when(achievementRepository.findAll(pageable)).thenReturn(page);

		// when
		Page<Achievement> result = achievementService.getAchievements(pageable);

		// then
		assertNotNull(result);
		assertEquals(2, result.getTotalElements());
		verify(achievementRepository).findAll(pageable);
	}

	@Test
	void testSearchAchievements() {
		// given: 제목 키워드 검색
		String keyword = "Test";
		Pageable pageable = PageRequest.of(0, 10);
		Achievement achievement = new Achievement();
		achievement.setId(UUID.randomUUID());
		achievement.setTitle("Test Achievement");

		Page<Achievement> page = new PageImpl<>(java.util.List.of(achievement), pageable, 1);
		when(achievementRepository.search(keyword, pageable)).thenReturn(page);

		// when
		Page<Achievement> result = achievementService.searchAchievements(keyword, pageable);

		// then
		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
		verify(achievementRepository).search(keyword, pageable);
	}

	@Test
	void testGetAchievementsByUser() {
		// given: 특정 사용자가 달성한 업적 검색
		UUID userId = UUID.randomUUID();
		Pageable pageable = PageRequest.of(0, 10);
		Achievement achievement = new Achievement();
		achievement.setId(UUID.randomUUID());
		achievement.setTitle("User Achievement");

		Page<Achievement> page = new PageImpl<>(java.util.List.of(achievement), pageable, 1);
		when(achievementUserRepository.search(userId, pageable)).thenReturn(page);

		// when
		Page<Achievement> result = achievementService.getAchivementsByUser(userId, pageable);

		// then
		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
		verify(achievementUserRepository).search(userId, pageable);
	}

	@Test
	void testDeleteAchievementSuccess() {
		// given: 삭제할 업적이 존재하는 경우
		UUID achievementId = UUID.randomUUID();
		Achievement achievement = new Achievement();
		achievement.setId(achievementId);
		achievement.setTitle("To Delete");

		when(achievementRepository.findById(achievementId)).thenReturn(Optional.of(achievement));

		// when
		Achievement result = achievementService.deleteAchievement(achievementId);

		// then: 삭제 플래그 설정 후 반환됨
		assertNotNull(result);
		assertEquals(achievementId, result.getId());
		verify(achievementRepository).findById(achievementId);
	}

	@Test
	void testDeleteAchievementFailure() {
		// given: 삭제할 업적이 존재하지 않는 경우
		UUID achievementId = UUID.randomUUID();
		when(achievementRepository.findById(achievementId)).thenReturn(Optional.empty());

		// when & then
		assertThrows(CustomBusinessException.class, () -> {
			achievementService.deleteAchievement(achievementId);
		});
	}
}