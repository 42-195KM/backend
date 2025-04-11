package com_42195km.msa.achievementservice.application.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com_42195km.msa.achievementservice.application.dto.request.CreateAchievementCommandDto;
import com_42195km.msa.achievementservice.domain.model.Achievement;

public interface AchivementService {
	// 업적 생성
	Achievement createAchievement(CreateAchievementCommandDto createAchievementCommandDto);

	// 업적 조회 by AchievementId
	Achievement getAchievementById(UUID achievementId);

	// 전체 업적 목록 조회
	Page<Achievement> getAchievements(Pageable pageable);

	// 업적 검색
	Page<Achievement> searchAchievements(String keyword, Pageable pageable);

	// 특정 사용자 업적 조회
	Page<Achievement> getAchivementsByUser(UUID userId, Pageable pageable);

	// 업적 삭제
	Achievement deleteAchievement(UUID achievementId);
}
