package com_42195km.msa.achievementservice.presentation.dto.response;

import com_42195km.msa.achievementservice.application.dto.request.CreateAchievementCommandDto;
import com_42195km.msa.achievementservice.domain.model.Achievement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class CreateAchievementResponseDto {
	private String title;
	private String description;
	private String criteria;
	private double criteriaValue;
	private String criteriaType;

	public CreateAchievementResponseDto(Achievement achievement) {
		this.title = achievement.getTitle();
		this.description = achievement.getDescription();
		this.criteria = achievement.getCriteria();
		this.criteriaValue = achievement.getCriteriaValue();
		this.criteriaType = achievement.getCriteriaInequality().toString();
	}
}
