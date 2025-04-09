package com._42195km.msa.competitionservice.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com._42195km.msa.common.exception.CustomBusinessException;
import com._42195km.msa.competitionservice.application.dto.request.CreateCompetitionCommandDto;
import com._42195km.msa.competitionservice.application.dto.response.CompetitionAppResponseDto;
import com._42195km.msa.competitionservice.application.mapper.CompetitionMapper;
import com._42195km.msa.competitionservice.domain.model.Competition;
import com._42195km.msa.competitionservice.infrastructure.persistence.CompetitionRepositoryImpl;
import com._42195km.msa.competitionservice.infrastructure.persistence.ParticipantRepositoryImpl;
import com._42195km.msa.competitionservice.application.exception.CompetitionServiceCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompetitionService {

	private final CompetitionRepositoryImpl competitionRepository;
	private final ParticipantRepositoryImpl participantRepository;
	private final CompetitionMapper competitionMapper;

	public void createCompetition(CreateCompetitionCommandDto command) {
		try {
			Competition competition = Competition.create(command);
			competitionRepository.save(competition);
		} catch (Exception e) {
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_CREATE_FAIL);
		}

	}

	public Page<CompetitionAppResponseDto> getCompetitions(Pageable pageable) {
		try {
			Page<Competition> competitions = competitionRepository.findAll(pageable);
			return competitionMapper.toAppResponseDtoPage(competitions);
		} catch (Exception e) {
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_GET_FAIL);
		}
	}
}
