package com._42195km.msa.competitionservice.presentation.dto.request;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplyCompetitionRequestDto {

	@Schema(example = "872dd912-8616-4595-aa94-5737bf633012")
	private  UUID competitionId;

	@Schema(example = "1845b196-7132-47c8-a233-193a6ebf5278")
	private UUID participantId;
}
