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
public class AchievementServiceImpl implements AchivementService{

	private final AchievementRepository achievementRepository;
	private final AchievementUserRepository achievementUserRepository;

	@Override
	public Achievement createAchievement(CreateAchievementCommandDto createAchievementCommandDto) {
		try{
			Achievement achievement = Achievement.createAchievement(createAchievementCommandDto);
			achievement = achievementRepository.save(achievement);
			return achievement;
		}
		catch (Exception e){
			throw CustomBusinessException.from(AchievementServiceCode.ACHIEVEMENT_CREATE_FAIL);
		}
	}

	@Override
	public Achievement getAchievementById(UUID achievementId) {
		try{
			Optional<Achievement> achievement = achievementRepository.findById(achievementId);
			return achievement.orElseThrow(() ->
				CustomBusinessException.from(AchievementServiceCode.ACHIEVEMENT_GET_FAIL));
		}
		catch (Exception e){
			throw CustomBusinessException.from(AchievementServiceCode.ACHIEVEMENT_GET_FAIL);
		}
	}

	@Override
	public Page<Achievement> getAchievements(Pageable pageable) {
		try{
			return achievementRepository.findAll(pageable);
		}
		catch (Exception e) {
			throw CustomBusinessException.from(AchievementServiceCode.ACHIEVEMENT_GET_ALL_FAIL);
		}
	}

	@Override
	public Page<Achievement> searchAchievements(String keyword, Pageable pageable) {
		try{
			return achievementRepository.search(keyword, pageable);
		}
		catch (Exception e) {
			throw CustomBusinessException.from(AchievementServiceCode.ACHIEVEMENT_SEARCH_FAIL);
		}
	}

	@Override
	public Page<Achievement> getAchivementsByUser(UUID userId, Pageable pageable) {
		try{
			return achievementUserRepository.search(userId, pageable);
		}
		catch (Exception e) {
			throw CustomBusinessException.from(AchievementServiceCode.ACHIEVEMENT_GET_BY_USER_FAIL);
		}
	}

	@Override
	public Achievement deleteAchievement(UUID achievementId) {
		try{
			Optional<Achievement> achievement = achievementRepository.findById(achievementId);
			achievement.ifPresent(BaseEntity::setDeleted);
			return achievement.orElseThrow(() ->
				CustomBusinessException.from(AchievementServiceCode.ACHIEVEMENT_DELETE_FAIL));
		}
		catch (Exception e) {
			throw CustomBusinessException.from(AchievementServiceCode.ACHIEVEMENT_DELETE_FAIL);
		}
	}
}
