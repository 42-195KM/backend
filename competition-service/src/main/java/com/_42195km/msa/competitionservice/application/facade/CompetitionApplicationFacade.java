package com._42195km.msa.competitionservice.application.facade;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com._42195km.msa.competitionservice.application.dto.CompleteAppDto;
import com._42195km.msa.competitionservice.application.dto.request.CreateCompetitionCommandDto;
import com._42195km.msa.competitionservice.application.dto.request.UpdateCompetitionCommandDto;
import com._42195km.msa.competitionservice.application.dto.response.CompetitionAppResponseDto;
import com._42195km.msa.competitionservice.application.dto.response.ParticipantAppResponseDto;
import com._42195km.msa.competitionservice.application.dto.response.SearchParticipantAppResponseDto;
import com._42195km.msa.competitionservice.application.service.CompetitionService;
import com._42195km.msa.competitionservice.application.service.ParticipantService;
import com._42195km.msa.competitionservice.application.service.SagaService;
import com._42195km.msa.competitionservice.presentation.dto.request.CancelParticipantRequestDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompetitionApplicationFacade {
	private final CompetitionService competitionService;
	private final ParticipantService participantService;
	private final SagaService sagaService;

	public void createCompetition(CreateCompetitionCommandDto command) {
		competitionService.createCompetition(command);
	}

	public Page<CompetitionAppResponseDto> getCompetitions(Pageable pageable) {
		return competitionService.getCompetitions(pageable);
	}

	public Page<CompetitionAppResponseDto> searchCompetitions(String keyword, Pageable pageable) {
		return competitionService.searchCompetition(keyword, pageable);
	}

	public CompetitionAppResponseDto getCompetition(UUID competitionId) {
		return competitionService.getCompetition(competitionId);
	}

	public Page<CompetitionAppResponseDto> getHostCompetitions(UUID userId, Pageable pageable) {
		return competitionService.getHostCompetition(userId, pageable);
	}

	public void updateCompetition(UUID competitionId, UpdateCompetitionCommandDto commandDto) {
		competitionService.updateCompetition(competitionId, commandDto);
	}

	public void deleteCompetition(UUID competitionId) {
		competitionService.deleteCompetition(competitionId);
	}

	// 대회 신청 관련 기능들 //

	/**
	 * 대회 신청 프로세스
	 */
	public String applyForCompetition(CompleteAppDto appDto) {
		return sagaService.processCompleteApplication(appDto);
	}

	/**
	 * 대회 신청 상태를 조회.
	 */
	public String getApplicationStatus(UUID competitionId, UUID participantId) {
		return sagaService.findActiveSagaId(competitionId, participantId);
	}

	/**
	 * 대회 추첨을 실행.
	 */
	public void drawCompetition(UUID competitionId) {
		competitionService.drawCompetition(competitionId);
	}

	// 참가자 관리 관련 기능들 //

	public Page<ParticipantAppResponseDto> getParticipants(Pageable pageable, UUID competitionId) {
		return participantService.getParticipants(pageable, competitionId);
	}

	public Page<SearchParticipantAppResponseDto> searchParticipants(String keyword, String searchType, Pageable pageable) {
		return participantService.searchParticipants(keyword, searchType, pageable);
	}

	public Page<SearchParticipantAppResponseDto> getParticipant(String keyword, Pageable pageable) {
		return participantService.getParticipant(keyword, pageable);
	}

	public void cancelParticipantByCompany(CancelParticipantRequestDto requestDto) {
		participantService.cancelParticipantByCompany(requestDto);
	}

	public void cancelParticipant(CancelParticipantRequestDto requestDto) {
		participantService.cancelParticipant(requestDto);
	}
}
