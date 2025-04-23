package com._42195km.msa.achievementservice.infrastructure.evaluator;

import com._42195km.msa.achievementservice.infrastructure.messaging.in.RunningRecordEventDto;
import com._42195km.msa.achievementservice.domain.model.Achievement;
import com._42195km.msa.achievementservice.domain.model.AchievementUser;
import com._42195km.msa.achievementservice.domain.model.CriteriaInequality;

public class AchievementRunningRecordEvaluator implements AchivementEvaluator<RunningRecordEventDto>{

	@Override
	public boolean isAlreadyAchieved(RunningRecordEventDto eventDto, Achievement achievement) {
		return achievement.getAchievementUsers().stream()
			.map(AchievementUser::getUserId)
			.anyMatch(id -> id.equals(eventDto.getUserId()));
	}

	@Override
	public boolean isAchievementMet(RunningRecordEventDto eventDto, Achievement achievement) {
		// 업적 기준 필드
		AchievementRunningRecordCriteriaField criteriaField =
			AchievementRunningRecordCriteriaField.fromKey(achievement.getCriteria());

		// 업적 기준 필드의 실제 값
		double actualValue = criteriaField.extract(eventDto);

		// 업적 기준 값
		double getCriteriaValue = achievement.getCriteriaValue();

		// 업적 기준 부등호
		CriteriaInequality criteriaInequality = achievement.getCriteriaInequality();

		// 기준 부등호에 따라 비교
		return criteriaInequality.compare(actualValue, getCriteriaValue);
	}
}
