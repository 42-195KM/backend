package com._42195km.msa.competitionservice.application.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com._42195km.msa.competitionservice.application.dto.request.CreateCompetitionCommandDto;
import com._42195km.msa.competitionservice.application.dto.request.UpdateCompetitionCommandDto;
import com._42195km.msa.competitionservice.application.dto.response.CompetitionAppResponseDto;

public interface CompetitionService {

	void createCompetition(CreateCompetitionCommandDto command);

	Page<CompetitionAppResponseDto> getCompetitions(Pageable pageable);

	Page<CompetitionAppResponseDto> searchCompetition(String keyword, Pageable pageable);

	CompetitionAppResponseDto getCompetition(UUID competitionId);

	Page<CompetitionAppResponseDto> getHostCompetition(UUID userId, Pageable pageable);

	void updateCompetition(UUID competitionId, UpdateCompetitionCommandDto commandDto);

	void deleteCompetition(UUID competitionId);

	boolean checkDuplicateApplication(UUID competitionId, UUID participantId);

	boolean checkCompetitionCapacity(UUID competitionId);

	void applyCompetition(UUID competitionId, UUID participantId);

	void drawCompetition(UUID competitionId);
}
