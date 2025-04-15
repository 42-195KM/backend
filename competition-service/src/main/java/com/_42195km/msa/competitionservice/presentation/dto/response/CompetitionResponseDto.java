package com._42195km.msa.competitionservice.presentation.dto.response;

import java.util.UUID;

import com._42195km.msa.competitionservice.domain.model.CompetitionType;
import com._42195km.msa.competitionservice.domain.model.ReceptionType;

import lombok.Getter;

@Getter
public class CompetitionResponseDto {
	private UUID id;
	private UUID userId;
	private String title;
	private CompetitionType type;
	private ReceptionType receptionType;
	private Integer participantsNum;
	private Integer price;

	public CompetitionResponseDto(UUID id, UUID userId, String title, CompetitionType type, ReceptionType receptionType, Integer participantsNum, Integer price) {
		this.id = id;
		this.userId = userId;
		this.title = title;
		this.type = type;
		this.receptionType = receptionType;
		this.participantsNum = participantsNum;
		this.price = price;
	}
}
