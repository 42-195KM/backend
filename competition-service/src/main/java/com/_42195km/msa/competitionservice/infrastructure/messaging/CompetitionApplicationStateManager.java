package com._42195km.msa.competitionservice.infrastructure.messaging;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com._42195km.msa.competitionservice.application.event.ApplicationStep;
import com._42195km.msa.competitionservice.application.event.CompetitionApplicationEvent;
import com._42195km.msa.competitionservice.application.service.ParticipantService;
import com._42195km.msa.competitionservice.domain.model.CompetitionApplicationData;

@Service
public class CompetitionApplicationStateManager {
	/*
	간단한 메모리 기반 상태 저장소
	TODO : Redis로 확장
	 */
	private final ConcurrentHashMap<String, CompetitionApplicationData> stateStore = new ConcurrentHashMap<>();

	private final ParticipantService participantService; // 최종 신청 처리 로직 호출

	public CompetitionApplicationStateManager(ParticipantService participantService) {
		this.participantService = participantService;
	}

	private String generateKey(String competitionId, String participantId) {
		return competitionId + ":" + participantId;
	}

	public void processEvent(CompetitionApplicationEvent event) {
		String key = generateKey(event.getCompetitionId().toString(), event.getParticipantId().toString());
		CompetitionApplicationData currentData = stateStore.getOrDefault(key,
			new CompetitionApplicationData(event.getCompetitionId(), event.getParticipantId()));

		if (event.getStep() == ApplicationStep.TERMS) {
			currentData.checkTerm(event.getTermsAgreed());
		} else if (event.getStep() == ApplicationStep.SOUVENIR) {
			currentData.checkSouvenirSelection(event.getSouvenirSelection());
		} else if (event.getStep() == ApplicationStep.SHIPPING) {
			currentData.checkShippingAddress(event.getShippingAddress());
		}
		stateStore.put(key, currentData);

		// 모든 단계의 데이터가 모였는지 확인
		if (currentData.getTermsAgreed() != null &&
			currentData.getSouvenirSelection() != null &&
			currentData.getShippingAddress() != null) {
			// 최종 처리: 집계된 데이터를 이용해 대회 신청 처리
			participantService.finalizeApplicationFromState(currentData);
			stateStore.remove(key);
		}
	}
}
