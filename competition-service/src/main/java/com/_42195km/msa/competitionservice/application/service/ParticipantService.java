package com._42195km.msa.competitionservice.application.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com._42195km.msa.common.exception.CustomBusinessException;
import com._42195km.msa.competitionservice.application.dto.response.ParticipantAppResponseDto;
import com._42195km.msa.competitionservice.application.dto.response.SearchParticipantAppResponseDto;
import com._42195km.msa.competitionservice.application.exception.CompetitionServiceCode;
import com._42195km.msa.competitionservice.application.mapper.ParticipantMapper;
import com._42195km.msa.competitionservice.domain.model.Competition;
import com._42195km.msa.competitionservice.domain.model.CompetitionApplicationData;
import com._42195km.msa.competitionservice.domain.model.CompetitionParticipantMapping;
import com._42195km.msa.competitionservice.domain.model.Participant;
import com._42195km.msa.competitionservice.infrastructure.persistence.CompetitionParticipantMappingRepositoryImpl;
import com._42195km.msa.competitionservice.infrastructure.persistence.CompetitionRepositoryImpl;
import com._42195km.msa.competitionservice.infrastructure.persistence.ParticipantRepositoryImpl;
import com._42195km.msa.competitionservice.presentation.dto.request.CancelParticipantRequestDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
			Page<CompetitionParticipantMapping> participants = mappingRepository.findParticipants(competitionId,
				pageable);
			return participantMapper.toParticipantAppResponseDtoPage(participants);
		} catch (Exception e) {
			log.error("대회 참가자 검색 실패 로그 : {}", e.getMessage());
			throw CustomBusinessException.from(CompetitionServiceCode.PARTICIPANT_GET_FAIL);
		}
	}

	public Page<SearchParticipantAppResponseDto> searchParticipants(String keyword, String searchType,
		Pageable pageable) {
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
				case "status":
					searchedPage = participantRepository.searchByStatus(keyword, pageable);
					break;
				case "uuid":
					try {
						UUID uuid = UUID.fromString(keyword);
						searchedPage = participantRepository.searchByUuid(keyword, pageable);
					} catch (IllegalArgumentException e) {
						log.error("대회 참가자 검색 실패 로그 : {}", e.getMessage());
						throw CustomBusinessException.from(CompetitionServiceCode.PARTICIPANT_SEARCH_FAIL);
					}
					break;
				default:
					throw CustomBusinessException.from(CompetitionServiceCode.PARTICIPANT_SEARCH_FAIL);
			}
			return searchedPage.map(participantMapper::toSearchParticipantAppResponseDto);
		} catch (Exception e) {
			log.error("검색 오류: {}, 검색 타입: {}, 키워드: {}",
				e.getMessage(), searchType, keyword);
			throw CustomBusinessException.from(CompetitionServiceCode.PARTICIPANT_SEARCH_FAIL);
		}
	}

	/*
	한 명이 여러 대회 신청했을 수도 있으니 Page로 리턴
	 */
	public Page<SearchParticipantAppResponseDto> getParticipant(String keyword, Pageable pageable) {
		try {
			Page<Object> participant = participantRepository.getByUuid(UUID.fromString(keyword), pageable);
			return participant.map(participantMapper::toSearchParticipantAppResponseDto);

		} catch (Exception e) {
			log.error(e.getMessage());
			throw CustomBusinessException.from(CompetitionServiceCode.PARTICIPANT_GET_FAIL);
		}
	}

	@Transactional
	public void cancelParticipantByCompany(CancelParticipantRequestDto requestDto) {
		try {
			CompetitionParticipantMapping participant = mappingRepository.findByCompetitionIdAndParticipantId(
				requestDto.getCompetitionId(), requestDto.getParticipantId());
			participant.cancel();
		} catch (Exception e) {
			log.error("신청 취소 디버깅 : {}", e.getMessage());
			throw CustomBusinessException.from(CompetitionServiceCode.PARTICIPANT_CANCEL_FAIL);
		}
	}

	public void cancelParticipant(CancelParticipantRequestDto requestDto) {
		try {
			CompetitionParticipantMapping participant = mappingRepository.findByCompetitionIdAndParticipantId(
				requestDto.getCompetitionId(), requestDto.getParticipantId());
			participant.cancel();
		} catch (Exception e) {
			log.error("신청 취소 디버깅 : {}", e.getMessage());
			throw CustomBusinessException.from(CompetitionServiceCode.PARTICIPANT_CANCEL_FAIL);
		}
	}

	@Transactional
	public void compensateApplyCompetition(UUID competitionId, UUID participantId) {
		try {
			cancelParticipant(new CancelParticipantRequestDto(competitionId, participantId, "일정변동",true));

			log.info("Compensated competition application: competitionId={}, participantId={}",
				competitionId, participantId);

		} catch (Exception e) {
			log.error("Compensation failed for competition application: {}", e.getMessage(), e);
			throw CustomBusinessException.from(CompetitionServiceCode.PARTICIPANT_CANCEL_FAIL);
		}
	}

	@Transactional
	public void compensateApplicationData(UUID competitionId, UUID participantId) {
		try {
			// 신청 데이터 상태 리셋 로직
			// TODO : Redis나 다른 저장소에서 삭제하는 로직 필요
			log.info("Compensated application data: competitionId={}, participantId={}",
				competitionId, participantId);

		} catch (Exception e) {
			log.error("Compensation failed for application data: {}", e.getMessage(), e);
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_APPLY_FAIL);
		}
	}

	@Transactional
	public void finalizeApplicationFromState(CompetitionApplicationData data) {
		try {
			// 대회 엔티티 조회
			Competition competition = competitionRepository.findById(data.getCompetitionId());
			// 참가자 엔티티 조회 및 생성
			Participant participant = participantRepository.findByParticipantId(data.getParticipantId());
			if (participant == null) {
				participant = new Participant(data.getParticipantId());
				participantRepository.save(participant);
			}
			// 중복 신청 체크
			Boolean checkDupl = mappingRepository.checkIsParticipate(data.getParticipantId(), data.getCompetitionId());
			if (checkDupl) {
				throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_APPLY_EXIST);
			}
			// 새로운 신청 매핑 생성 및 각 단계 데이터 반영
			CompetitionParticipantMapping mapping = CompetitionParticipantMapping.create(competition, participant);
			mapping.checkTerm(data.getTermsAgreed());
			mapping.checkSouvenirSelection(data.getSouvenirSelection());
			mapping.checkShippingAddress(data.getShippingAddress());
			mappingRepository.save(mapping);
		} catch (CustomBusinessException e) {
			throw e;
		} catch (Exception e) {
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_APPLY_FAIL);
		}
	}
}
