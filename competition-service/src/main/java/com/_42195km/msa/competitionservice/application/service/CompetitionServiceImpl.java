package com._42195km.msa.competitionservice.application.service;

import java.util.Arrays;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com._42195km.msa.common.exception.CustomBusinessException;
import com._42195km.msa.competitionservice.application.dto.request.CreateCompetitionCommandDto;
import com._42195km.msa.competitionservice.application.dto.request.UpdateCompetitionCommandDto;
import com._42195km.msa.competitionservice.application.dto.response.CompetitionAppResponseDto;
import com._42195km.msa.competitionservice.application.exception.CompetitionServiceCode;
import com._42195km.msa.competitionservice.application.mapper.CompetitionMapper;
import com._42195km.msa.competitionservice.domain.model.Competition;
import com._42195km.msa.competitionservice.domain.model.CompetitionParticipantMapping;
import com._42195km.msa.competitionservice.domain.model.CompetitionType;
import com._42195km.msa.competitionservice.domain.model.Participant;
import com._42195km.msa.competitionservice.domain.model.ReceptionType;
import com._42195km.msa.competitionservice.infrastructure.persistence.CompetitionParticipantMappingRepositoryImpl;
import com._42195km.msa.competitionservice.infrastructure.persistence.CompetitionRepositoryImpl;
import com._42195km.msa.competitionservice.infrastructure.persistence.ParticipantRepositoryImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompetitionServiceImpl implements CompetitionService {

	private final CompetitionRepositoryImpl competitionRepository;
	private final ParticipantRepositoryImpl participantRepository;
	private final CompetitionParticipantMappingRepositoryImpl mappingRepository;
	private final CompetitionMapper competitionMapper;

	@Override
	public void createCompetition(CreateCompetitionCommandDto command) {
		try {
			Competition competition = Competition.create(command);
			competitionRepository.save(competition);
		} catch (Exception e) {
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_CREATE_FAIL);
		}
	}

	@Override
	public Page<CompetitionAppResponseDto> getCompetitions(Pageable pageable) {
		try {
			Page<Competition> competitions = competitionRepository.findAll(pageable);
			return competitionMapper.toAppResponseDtoPage(competitions);
		} catch (Exception e) {
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_GET_FAIL);
		}
	}

	@Override
	public Page<CompetitionAppResponseDto> searchCompetition(String keyword, Pageable pageable) {

		try {
			Page<Competition> competition;

			// 1. CompetitionType enum에 해당하는지 확인
			boolean isCompetitionType = Arrays.stream(CompetitionType.values())
				.anyMatch(type -> type.name().equals(keyword));

			// 2. ReceptionType enum에 해당하는지 확인
			boolean isReceptionType = Arrays.stream(ReceptionType.values())
				.anyMatch(type -> type.name().equals(keyword));

			// enum 타입에 맞는 검색
			if (isCompetitionType || isReceptionType) {
				competition = competitionRepository.searchByEnumType(keyword, pageable);
			} else {
				competition = competitionRepository.searchByTitle(keyword, pageable);
			}

			return competitionMapper.toAppResponseDtoPage(competition);
		} catch (Exception e) {
			log.error("error check : {}", e.getMessage());
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_GET_FAIL);
		}
	}

	@Override
	public CompetitionAppResponseDto getCompetition(UUID competitionId) {
		try {
			Competition competition = competitionRepository.findById(competitionId);
			return competitionMapper.toAppResponseDto(competition);
		} catch (CustomBusinessException e) {
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_GET_ID_FAIL);
		} catch (Exception e) {
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_GET_FAIL);
		}
	}

	@Override
	public Page<CompetitionAppResponseDto> getHostCompetition(UUID userId, Pageable pageable) {
		try {
			Page<Competition> competitions = competitionRepository.findByHost(userId, pageable);
			return competitionMapper.toAppResponseDtoPage(competitions);
		} catch (Exception e) {
			log.error("error check : {}", e.getMessage());
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_GET_FAIL);
		}
	}

	@Override
	@Transactional
	public void updateCompetition(UUID competitionId, UpdateCompetitionCommandDto commandDto) {
		try {
			Competition competition = competitionRepository.findById(competitionId);
			competition.update(commandDto);
		} catch (Exception e) {
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_UPDATE_FAIL);
		}
	}

	@Override
	@Transactional
	public void deleteCompetition(UUID competitionId) {
		try {
			Competition competition = competitionRepository.findById(competitionId);
			competition.setDeleted();
		} catch (Exception e) {
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_UPDATE_FAIL);
		}
	}

	@Override
	public boolean checkDuplicateApplication(UUID competitionId, UUID participantId) {
		return mappingRepository.checkIsParticipate(participantId, competitionId);
	}

	@Override
	public boolean checkCompetitionCapacity(UUID competitionId) {
		Competition competition = competitionRepository.findById(competitionId);

		if (competition.getReceptionType() == ReceptionType.FIRST) {
			long ParticipantCount = mappingRepository.countByCompetition(competition);
			log.error("참가자 수 확인 : {}", ParticipantCount);

			if (ParticipantCount > competition.getParticipantsNum()) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	@Override
	@Transactional
	public void applyCompetition(UUID competitionId, UUID participantId) {
		try {
			Competition competition = competitionRepository.findById(competitionId);

			Participant participant = participantRepository.findByParticipantId(participantId);
			if (participant == null) {
				participant = new Participant(participantId);
				participantRepository.save(participant);
			}

			CompetitionParticipantMapping apply = CompetitionParticipantMapping.create(competition, participant);
			mappingRepository.save(apply);

		} catch (CustomBusinessException e) {
			throw e;//CustomBusinessException.from(CompetitionServiceCode.COMPETITION_APPLY_EXIST);
		} catch (Exception e) {
			log.error("대회 신청 실패", e);
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_APPLY_FAIL);
		}
	}

	@Override
	@Transactional
	public void drawCompetition(UUID competitionId) {
		try {
			Competition competition = competitionRepository.findById(competitionId);
			competition.performDraw();
			competitionRepository.save(competition);
			log.info("대회 추첨 완료: {}", competitionId);
		} catch (Exception e) {
			log.error("대회 추첨 중 오류 발생: {}", e.getMessage());
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_DRAW_FAIL);
		}
	}

}
