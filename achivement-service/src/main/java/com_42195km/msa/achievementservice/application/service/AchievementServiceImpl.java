package com_42195km.msa.achievementservice.application.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com_42195km.msa.achievementservice.application.dto.request.CreateAchievementCommandDto;
import com_42195km.msa.achievementservice.domain.model.Achievement;
import com_42195km.msa.achievementservice.domain.repository.AchievementRepository;
import com_42195km.msa.achievementservice.domain.repository.AchievementUserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AchievementServiceImpl implements AchivementService{

	private final AchievementRepository achievementRepository;
	private final AchievementUserRepository achievementUserRepository;

	@Override
	public Achievement createAchievement(CreateAchievementCommandDto createAchievementCommandDto) {
		return null;
	}

	@Override
	public Achievement getAchievementById(UUID achievementId) {
		return null;
	}

	@Override
	public Page<Achievement> getAchievements(Pageable pageable) {
		return null;
	}

	@Override
	public Page<Achievement> searchAchievements(String keyword, Pageable pageable) {
		return null;
	}

	@Override
	public Page<Achievement> getAchivementsByUser(UUID userId, Pageable pageable) {
		return null;
	}

	@Override
	public Achievement deleteAchievement(UUID achievementId) {
		return null;
	}
}
