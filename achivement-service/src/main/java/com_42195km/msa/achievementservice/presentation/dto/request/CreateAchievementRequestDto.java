package com_42195km.msa.achievementservice.presentation.dto.request;

import com_42195km.msa.achievementservice.application.dto.request.CreateAchievementCommandDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateAchievementRequestDto {
	private String title;
	private String description;
	private String criteria;
	private double criteriaValue;
	private String criteriaType;

	public CreateAchievementCommandDto toCommandDto() {
		return new CreateAchievementCommandDto(title, description, criteria, criteriaValue, criteriaType);
	}
}
