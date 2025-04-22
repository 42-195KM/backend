package com._42195km.msa.competitionservice.application.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com._42195km.msa.competitionservice.application.dto.response.ParticipantAppResponseDto;
import com._42195km.msa.competitionservice.application.dto.response.SearchParticipantAppResponseDto;
import com._42195km.msa.competitionservice.presentation.dto.request.CancelParticipantRequestDto;

public interface ParticipantService {

	Page<ParticipantAppResponseDto> getParticipants(Pageable pageable, UUID competitionId);

	Page<SearchParticipantAppResponseDto> searchParticipants(String keyword, String searchType, Pageable pageable);

	Page<SearchParticipantAppResponseDto> getParticipant(String keyword, Pageable pageable);

	void cancelParticipantByCompany(CancelParticipantRequestDto requestDto);

	void cancelParticipant(CancelParticipantRequestDto requestDto);

}
