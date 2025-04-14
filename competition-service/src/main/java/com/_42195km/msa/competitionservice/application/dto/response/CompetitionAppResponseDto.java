package com._42195km.msa.competitionservice.application.dto.response;

import java.util.UUID;

import com._42195km.msa.competitionservice.domain.model.Competition;
import com._42195km.msa.competitionservice.domain.model.CompetitionType;
import com._42195km.msa.competitionservice.domain.model.ReceptionType;

import lombok.Getter;

@Getter
public class CompetitionAppResponseDto {
	private UUID id;
	private UUID userId;
	private String title;
	private CompetitionType type;
	private ReceptionType receptionType;
	private Integer participantsNum;
	private Integer price;

	public CompetitionAppResponseDto(UUID id, UUID userId, String titile, CompetitionType type, ReceptionType receptionType, Integer participantsNum, Integer price) {
		this.id = id;
		this.userId = userId;
		this.title = titile;
		this.type = type;
		this.receptionType = receptionType;
		this.participantsNum = participantsNum;
		this.price = price;
	}

	public static CompetitionAppResponseDto fromEntity(Competition competition) {
		return new CompetitionAppResponseDto(
			competition.getId(),
			competition.getUserId(),
			competition.getTitle(),
			competition.getType(),
			competition.getReceptionType(),
			competition.getParticipantsNum(),
			competition.getPrice()
		);
	}

}
