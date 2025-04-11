package com_42195km.msa.achievementservice.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com_42195km.msa.achievementservice.domain.model.Achievement;
import com_42195km.msa.achievementservice.domain.repository.AchievementRepository;

@Repository
public interface AchievementJpaRepository extends AchievementRepository, JpaRepository<Achievement, UUID> {
	@Override
	default Optional<Achievement> findById(UUID achivementId){
		return findByIdAndIsDeletedFalse(achivementId);
	}

	Optional<Achievement> findByIdAndIsDeletedFalse(UUID achivementId);

	@Override
	@Query("SELECT a FROM Achievement a "
		+ "WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :keyWord, '%'))")
	Page<Achievement> search(@Param("keyWord") String keyWord, Pageable pageable);
}
