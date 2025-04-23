package com._42195km.msa.achievementservice.infrastructure.evaluator;

import com._42195km.msa.achievementservice.domain.model.Achievement;

public interface AchivementEvaluator<E> {
	/** 이미 달성한 유저인지 */
	boolean isAlreadyAchieved(E eventDto, Achievement achievement);

	/** 업적 기준을 만족하는지 */
	boolean isAchievementMet(E eventDto, Achievement achievement);
}
