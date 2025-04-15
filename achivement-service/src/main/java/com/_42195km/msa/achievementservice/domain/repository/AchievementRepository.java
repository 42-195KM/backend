package com._42195km.msa.achievementservice.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com._42195km.msa.achievementservice.domain.model.Achievement;

public interface AchievementRepository {
	Achievement save(Achievement achievement);
	Optional<Achievement> findById(UUID achivementId);
	Page<Achievement> findAll(Pageable pageable);
	Page<Achievement> search(String keyWord, Pageable pageable);
	List<Achievement> findAll();
}
