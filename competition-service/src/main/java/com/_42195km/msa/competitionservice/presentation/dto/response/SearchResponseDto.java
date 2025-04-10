package com._42195km.msa.competitionservice.presentation.dto.response;

import java.util.UUID;

import com._42195km.msa.competitionservice.domain.model.CompetitionType;
import com._42195km.msa.competitionservice.domain.model.ReceptionType;
import com._42195km.msa.competitionservice.domain.model.Statue;

import lombok.Getter;

@Getter
public class SearchResponseDto {
	private UUID competitionID;
	private String title;
	private CompetitionType competitionType;
	private ReceptionType receptionType;
	private UUID participantID;
	private Statue statue;

	public SearchResponseDto(UUID competitionID, String title, CompetitionType competitionType, ReceptionType receptionType, UUID participantID, Statue statue) {
		this.competitionID = competitionID;
		this.title = title;
		this.competitionType = competitionType;
		this.receptionType = receptionType;
		this.participantID = participantID;
		this.statue = statue;

	}
}
