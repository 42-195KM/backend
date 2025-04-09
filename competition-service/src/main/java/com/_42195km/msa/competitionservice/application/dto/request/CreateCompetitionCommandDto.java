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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CreateCompetitionCommandDto {

	private UUID userId;

	private String title;

	private CompetitionType type;

	private ReceptionType receptionType;

	private Integer participantsNum;

	private Integer price;

}
