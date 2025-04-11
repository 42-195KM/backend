package com._42195km.msa.competitionservice.application.dto.request;

import java.util.UUID;

import com._42195km.msa.competitionservice.domain.model.CompetitionType;
import com._42195km.msa.competitionservice.domain.model.ReceptionType;
import com._42195km.msa.competitionservice.presentation.dto.request.CreateCompetitionRequestDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
public class CreateCompetitionCommandDto {

	private UUID userId;

	private String title;

	private CompetitionType type;

	private ReceptionType receptionType;

	private Integer participantsNum;

	private Integer price;

	public CreateCompetitionCommandDto(UUID userId, String title, CompetitionType type, ReceptionType receptionType, Integer participantsNum, Integer price) {
		this.userId = userId;
		this.title = title;
		this.type = type;
		this.receptionType = receptionType;
		this.participantsNum = participantsNum;
		this.price = price;
	}

	public static CreateCompetitionCommandDto from(CreateCompetitionRequestDto dto) {
		return new CreateCompetitionCommandDto(
			dto.getUserId(),
			dto.getTitle(),
			dto.getType(),
			dto.getReceptionType(),
			dto.getParticipantsNum(),
			dto.getPrice()
		);
	}

}
