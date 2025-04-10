package com._42195km.msa.competitionservice.application.mapper;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com._42195km.msa.competitionservice.application.dto.response.CompetitionAppResponseDto;
import com._42195km.msa.competitionservice.application.dto.response.ParticipantAppResponseDto;
import com._42195km.msa.competitionservice.application.dto.response.SearchParticipantAppResponseDto;
import com._42195km.msa.competitionservice.domain.model.CompetitionType;
import com._42195km.msa.competitionservice.domain.model.Participant;
import com._42195km.msa.competitionservice.domain.model.ReceptionType;
import com._42195km.msa.competitionservice.domain.model.Statue;
import com._42195km.msa.competitionservice.presentation.dto.response.CompetitionResponseDto;
import com._42195km.msa.competitionservice.presentation.dto.response.ParticipantResponseDto;
import com._42195km.msa.competitionservice.presentation.dto.response.SearchResponseDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ParticipantMapper {

	public ParticipantAppResponseDto toParticipantAppResponseDto(Participant participant) {
		return ParticipantAppResponseDto.builder().participantId(participant.getParticipantId()).statue(
			String.valueOf(participant.getStatue())).build();
	}

	public Page<ParticipantAppResponseDto> toParticipantAppResponseDtoPage(Page<Participant> participants) {
		return participants.map(participant -> toParticipantAppResponseDto(participant));
	}

	public ParticipantResponseDto toParticipantResponseDto(ParticipantAppResponseDto participantAppResponseDto) {
		return ParticipantResponseDto.builder()
			.participantId(participantAppResponseDto.getParticipantId())
			.statue(participantAppResponseDto.getStatue())
			.build();
	}

	public Page<ParticipantResponseDto> toParticipantResponseDtoPage(Page<ParticipantAppResponseDto> appResponseDtos) {
		return appResponseDtos.map(participant -> toParticipantResponseDto(participant));
	}

	public SearchParticipantAppResponseDto toSearchParticipantAppResponseDto(Object object) {
		Object[] arr = (Object[]) object;
		return SearchParticipantAppResponseDto.builder()
			.competitionID(arr[0] != null ? (UUID) arr[0] : null)
			.title(arr[1] != null ? (String) arr[1] : null)
			.competitionType(arr[2] != null ? (CompetitionType) arr[2] : null)
			.receptionType(arr[3] != null ? (ReceptionType) arr[3] : null)
			.participantID(arr[4] != null ? (UUID) arr[4] : null)
			.statue(arr[5] != null ? (Statue) arr[5] : null)
			.build();
	}

	public Page<SearchResponseDto> toPresentationDtoPage(Page<SearchParticipantAppResponseDto> appResponseDtos) {
		return appResponseDtos.map(dto -> new SearchResponseDto(
			dto.getCompetitionID(),
			dto.getTitle(),
			dto.getCompetitionType(),
			dto.getReceptionType(),
			dto.getParticipantID(),
			dto.getStatue()
		));
	}
}
