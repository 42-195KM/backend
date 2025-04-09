package com._42195km.msa.competitionservice.presentation.dto.request;

import com._42195km.msa.competitionservice.application.dto.request.UpdateCompetitionCommandDto;
import com._42195km.msa.competitionservice.domain.model.CompetitionType;
import com._42195km.msa.competitionservice.domain.model.ReceptionType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class UpdateCompetitionRequestDto {
	@Schema(example = "춘천 마라톤")
	private String title;

	@Schema(example = "FULL")
	private CompetitionType type;

	@Schema(example = "FIRST")
	private ReceptionType receptionType;

	@Schema(example = "30000")
	private Integer participantsNum;

	@Schema(example = "100000")
	private Integer price;

	public UpdateCompetitionCommandDto toCommandDto() {
		return new UpdateCompetitionCommandDto(title, type, receptionType, participantsNum, price);
	}
}
