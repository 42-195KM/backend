package com._42195km.msa.competitionservice.application.service;

import java.util.Objects;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com._42195km.msa.common.exception.CustomBusinessException;
import com._42195km.msa.competitionservice.application.dto.response.ParticipantAppResponseDto;
import com._42195km.msa.competitionservice.application.dto.response.SearchParticipantAppResponseDto;
import com._42195km.msa.competitionservice.application.exception.CompetitionServiceCode;
import com._42195km.msa.competitionservice.application.mapper.ParticipantMapper;
import com._42195km.msa.competitionservice.domain.model.Competition;
import com._42195km.msa.competitionservice.domain.model.Participant;
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
		try {
			Competition competition = competitionRepository.findById(competitionId);
			Page<Participant> participants = mappingRepository.findParticipants(competitionId, pageable);
			return participantMapper.toParticipantAppResponseDtoPage(participants);
		} catch (Exception e) {
			throw CustomBusinessException.from(CompetitionServiceCode.PARTICIPANT_GET_FAIL);
		}
	}

	public Page<SearchParticipantAppResponseDto> searchParticipants(String keyword, String searchType, Pageable pageable) {
		try {
			Page<Object> searchedPage;
			switch (searchType.toLowerCase()) {
				case "title":
					searchedPage = participantRepository.searchByTitle(keyword, pageable);
					break;
				case "competitiontype":
					searchedPage = participantRepository.searchByCompetitionType(keyword, pageable);
					break;
				case "receptiontype":
					searchedPage = participantRepository.searchByReceptionType(keyword, pageable);
					break;
				case "statue":
					searchedPage = participantRepository.searchByStatue(keyword, pageable);
					break;
				case "uuid":
					searchedPage = participantRepository.searchByUuid(UUID.fromString(keyword), pageable);
					break;
				default:
					searchedPage = participantRepository.searchByUuid(UUID.fromString(keyword), pageable);
			}
			return searchedPage.map(participantMapper::toSearchParticipantAppResponseDto);
		} catch (Exception e) {
			throw CustomBusinessException.from(CompetitionServiceCode.PARTICIPANT_SEARCH_FAIL);
		}
	}
}
