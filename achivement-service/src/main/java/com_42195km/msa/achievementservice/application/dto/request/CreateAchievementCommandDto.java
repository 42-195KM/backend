package com_42195km.msa.achievementservice.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateAchievementCommandDto {
	private String title;
	private String description;
	private String criteria;
	private double criteriaValue;
	private String criteriaType;
}
