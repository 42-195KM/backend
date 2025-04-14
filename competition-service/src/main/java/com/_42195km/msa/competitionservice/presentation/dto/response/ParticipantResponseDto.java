package com._42195km.msa.competitionservice.presentation.dto.response;

import java.util.UUID;

import lombok.Builder;

public class ParticipantResponseDto {

	private UUID participantId;
	private String statue;

	@Builder
	public ParticipantResponseDto(UUID participantId, String statue) {
		this.participantId = participantId;
		this.statue = statue;
	}
}
