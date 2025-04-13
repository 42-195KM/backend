package com._42195km.msa.competitionservice.application.dto.request;

import com._42195km.msa.competitionservice.domain.model.CompetitionType;
import com._42195km.msa.competitionservice.domain.model.ReceptionType;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateCompetitionCommandDto {
	private String title;
	private CompetitionType type;
	private ReceptionType receptionType;
	private Integer participantsNum;
	private Integer price;

	public UpdateCompetitionCommandDto(String title, CompetitionType type, ReceptionType receptionType, Integer participantsNum, Integer price) {
		this.title = title;
		this.type = type;
		this.receptionType = receptionType;
		this.participantsNum = participantsNum;
		this.price = price;
	}

}
