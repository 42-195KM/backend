package com._42195km.msa.competitionservice.application.dto.response;

import java.util.UUID;

import com._42195km.msa.competitionservice.domain.model.Status;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ParticipantAppResponseDto {

	private UUID participantId;
	private Status status;

	@Builder
	public ParticipantAppResponseDto(UUID participantId, Status status) {
		this.participantId = participantId;
		this.status = status;
	}
}
