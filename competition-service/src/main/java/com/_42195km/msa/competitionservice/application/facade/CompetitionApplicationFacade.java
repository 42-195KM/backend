package com._42195km.msa.competitionservice.application.facade;

import org.springframework.stereotype.Service;

import com._42195km.msa.competitionservice.application.service.CompetitionService;
import com._42195km.msa.competitionservice.application.service.ParticipantService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompetitionApplicationFacade {
	private final CompetitionService competitionService;
	private final ParticipantService participantService;
}
