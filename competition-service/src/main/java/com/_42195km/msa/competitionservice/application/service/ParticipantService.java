package com._42195km.msa.competitionservice.application.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com._42195km.msa.competitionservice.application.dto.response.ParticipantAppResponseDto;
import com._42195km.msa.competitionservice.application.mapper.ParticipantMapper;
import com._42195km.msa.competitionservice.domain.model.Competition;
import com._42195km.msa.competitionservice.domain.model.Participant;
import com._42195km.msa.competitionservice.domain.repository.ParticipantRepository;
import com._42195km.msa.competitionservice.infrastructure.persistence.CompetitionParticipantMappingRepositoryImpl;
import com._42195km.msa.competitionservice.infrastructure.persistence.CompetitionRepositoryImpl;
import com._42195km.msa.competitionservice.infrastructure.persistence.ParticipantRepositoryImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParticipantService {

	private final ParticipantRepositoryImpl participantRepository;
	private final CompetitionRepositoryImpl competitionRepository;
	private final CompetitionParticipantMappingRepositoryImpl mappingRepository;
	private final ParticipantMapper participantMapper;

	public Page<ParticipantAppResponseDto> getParticipants(Pageable pageable, UUID competitionId) {
		Competition competition = competitionRepository.findById(competitionId);
		Page<Participant> participants = mappingRepository.findParticipants(competitionId, pageable);
		return participantMapper.toParticipantAppResponseDtoPage(participants);
	}
}
