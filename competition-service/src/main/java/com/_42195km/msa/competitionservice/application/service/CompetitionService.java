package com._42195km.msa.competitionservice.application.service;

import java.util.Arrays;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com._42195km.msa.common.exception.CustomBusinessException;
import com._42195km.msa.competitionservice.application.dto.request.CreateCompetitionCommandDto;
import com._42195km.msa.competitionservice.application.dto.response.CompetitionAppResponseDto;
import com._42195km.msa.competitionservice.application.exception.CompetitionServiceCode;
import com._42195km.msa.competitionservice.application.mapper.CompetitionMapper;
import com._42195km.msa.competitionservice.domain.model.Competition;
import com._42195km.msa.competitionservice.domain.model.CompetitionType;
import com._42195km.msa.competitionservice.domain.model.ReceptionType;
import com._42195km.msa.competitionservice.infrastructure.persistence.CompetitionRepositoryImpl;
import com._42195km.msa.competitionservice.infrastructure.persistence.ParticipantRepositoryImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

	public Page<CompetitionAppResponseDto> getCompetition(String keyword, Pageable pageable) {

		try {
			Page<Competition> competition;

			// 1. CompetitionType enum에 해당하는지 확인
			boolean isCompetitionType = Arrays.stream(CompetitionType.values())
				.anyMatch(type -> type.name().equals(keyword));

			// 2. ReceptionType enum에 해당하는지 확인
			boolean isReceptionType = Arrays.stream(ReceptionType.values())
				.anyMatch(type -> type.name().equals(keyword));

			if (isCompetitionType || isReceptionType) {
				// enum 타입에 맞는 검색
				competition = competitionRepository.searchByEnumType(keyword, pageable);
			} else {
				// 일반 문자열 검색 (제목만)
				competition = competitionRepository.searchByTitle(keyword, pageable);
			}

			return competitionMapper.toAppResponseDtoPage(competition);
		} catch (Exception e) {
			log.error("error check : {}", e.getMessage());
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_GET_FAIL);
		}
	}
}
