package com_42195km.msa.achievementservice.presentation.dto.response;

import java.util.UUID;

import com_42195km.msa.achievementservice.domain.model.Achievement;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetAchievementResponseDto {
	private UUID id;
	private String title;
	private String description;
	private String criteria;
	private double criteriaValue;
	private String criteriaType;

	public GetAchievementResponseDto(Achievement achievement) {
		this.id = achievement.getId();
		this.title = achievement.getTitle();
		this.description = achievement.getDescription();
		this.criteria = achievement.getCriteria();
		this.criteriaValue = achievement.getCriteriaValue();
		this.criteriaType = achievement.getCriteriaInequality().toString();
	}
}
