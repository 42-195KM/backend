package com_42195km.msa.achievementservice.infrastructure.persistence;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com_42195km.msa.achievementservice.domain.model.Achievement;
import com_42195km.msa.achievementservice.domain.model.AchievementUser;
import com_42195km.msa.achievementservice.domain.repository.AchievementUserRepository;

public interface AchievementUserJpaRepository extends AchievementUserRepository, JpaRepository<AchievementUser, UUID> {
	@Override
	@Query("SELECT au.achievement FROM AchievementUser au WHERE au.userId = :userId")
	Page<Achievement> search(@Param("userId") UUID userId, Pageable pageable);
}
