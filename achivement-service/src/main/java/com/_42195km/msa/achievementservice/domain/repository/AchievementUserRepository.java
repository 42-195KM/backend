package com._42195km.msa.achievementservice.domain.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com._42195km.msa.achievementservice.domain.model.Achievement;
import com._42195km.msa.achievementservice.domain.model.AchievementUser;

public interface AchievementUserRepository {
	Page<Achievement> search(UUID userId, Pageable pageable);
	AchievementUser save(AchievementUser achievementUser);
}
