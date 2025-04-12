package com._42195km.msa.competitionservice.presentation.dto.request;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class SouvenirSelectionRequestDto {
	private UUID competitionId;
	private UUID participantId;
	@Schema(example = "optionA")
	private String souvenirSelection;

}
