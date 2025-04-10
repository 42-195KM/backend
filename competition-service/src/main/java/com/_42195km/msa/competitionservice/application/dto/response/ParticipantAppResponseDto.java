package com._42195km.msa.competitionservice.application.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ParticipantAppResponseDto {

	private UUID participantId;
	private String statue;

	@Builder
	public ParticipantAppResponseDto(UUID participantId, String statue) {
		this.participantId = participantId;
		this.statue = statue;
	}
}
