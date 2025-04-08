package com._42195km.msa.competitionservice.presentation.dto.request;

import java.util.UUID;

import com._42195km.msa.competitionservice.application.dto.request.CreateCompetitionCommandDto;
import com._42195km.msa.competitionservice.domain.model.CompetitionType;
import com._42195km.msa.competitionservice.domain.model.ReceptionType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class CreateCompetitionRequestDto {

	@Schema(example = "1845b196-7132-47c8-a233-193a6ebf5278")
	private UUID userId;

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

	public CreateCompetitionCommandDto toCommandDto() {
		return new CreateCompetitionCommandDto(userId,title,type,receptionType,participantsNum,price);
	}
}
