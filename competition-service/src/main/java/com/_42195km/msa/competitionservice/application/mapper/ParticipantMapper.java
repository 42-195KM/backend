package com._42195km.msa.competitionservice.application.mapper;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com._42195km.msa.competitionservice.application.dto.response.ParticipantAppResponseDto;
import com._42195km.msa.competitionservice.domain.model.Participant;

@Component
public class ParticipantMapper {

	public ParticipantAppResponseDto toParticipantAppResponseDto(Participant participant) {
		return ParticipantAppResponseDto.builder().participantId(participant.getParticipantId()).statue(
			String.valueOf(participant.getStatue())).build();
	}

	public Page<ParticipantAppResponseDto> toParticipantAppResponseDtoPage(Page<Participant> participants) {
		return participants.map(participant -> toParticipantAppResponseDto(participant));
	}
}
