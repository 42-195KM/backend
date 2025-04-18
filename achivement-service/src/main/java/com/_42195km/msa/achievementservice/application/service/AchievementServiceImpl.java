package com._42195km.msa.achievementservice.application.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com._42195km.msa.common.BaseEntity;
import com._42195km.msa.common.exception.CustomBusinessException;

import com._42195km.msa.achievementservice.application.dto.request.CreateAchievementCommandDto;
import com._42195km.msa.achievementservice.domain.model.Achievement;
import com._42195km.msa.achievementservice.domain.repository.AchievementRepository;
import com._42195km.msa.achievementservice.domain.repository.AchievementUserRepository;
import com._42195km.msa.achievementservice.infrastructure.code.AchievementServiceCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AchievementServiceImpl extends ServiceExecutor implements AchivementService{

	private final AchievementRepository achievementRepository;
	private final AchievementUserRepository achievementUserRepository;

	@Override
	public Achievement createAchievement(CreateAchievementCommandDto createAchievementCommandDto) {
		return execute(() -> {
			Achievement achievement = Achievement.createAchievement(createAchievementCommandDto);
			achievement = achievementRepository.save(achievement);
			return achievement;
		}, AchievementServiceCode.ACHIEVEMENT_CREATE_FAIL);
	}

	@Override
	public Achievement getAchievementById(UUID achievementId) {
		return execute(() -> {
			Optional<Achievement> achievement = achievementRepository.findById(achievementId);
			return achievement.orElseThrow(() ->
				CustomBusinessException.from(AchievementServiceCode.ACHIEVEMENT_GET_FAIL));
		}, AchievementServiceCode.ACHIEVEMENT_GET_FAIL);
	}

	@Override
	public Page<Achievement> getAchievements(Pageable pageable) {
		return execute(() ->
			achievementRepository.findAll(pageable),
			AchievementServiceCode.ACHIEVEMENT_GET_ALL_FAIL);
	}

	@Override
	public Page<Achievement> searchAchievements(String keyword, Pageable pageable) {
		return execute(() ->
			achievementRepository.search(keyword, pageable),
			AchievementServiceCode.ACHIEVEMENT_SEARCH_FAIL);
	}

	@Override
	public Page<Achievement> getAchivementsByUser(UUID userId, Pageable pageable) {
		return execute(() ->
			achievementUserRepository.search(userId, pageable),
			AchievementServiceCode.ACHIEVEMENT_GET_BY_USER_FAIL);
	}

	@Override
	public Achievement deleteAchievement(UUID achievementId) {
		return execute(() -> {
			Optional<Achievement> achievement = achievementRepository.findById(achievementId);
			achievement.ifPresent(BaseEntity::setDeleted);
			return achievement.orElseThrow(() ->
				CustomBusinessException.from(AchievementServiceCode.ACHIEVEMENT_DELETE_FAIL));
		}, AchievementServiceCode.ACHIEVEMENT_DELETE_FAIL);
	}
}
