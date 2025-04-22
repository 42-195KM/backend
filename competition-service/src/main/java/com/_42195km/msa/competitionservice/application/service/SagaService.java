package com._42195km.msa.competitionservice.application.service;

import java.util.UUID;

import com._42195km.msa.competitionservice.application.dto.CompleteAppDto;

public interface SagaService {

	/*
	주어진 대회 ID와 참가자 ID에 해당하는 활성 Saga를 찾거나 새로 생성
	 */
	String findOrCreateSagaId(UUID competitionId, UUID participantId);

	/*
	대회 신청 전체 프로세스를 관리
	 */
	String processCompleteApplication(CompleteAppDto requestDto);

	/*
	주어진 대회 ID와 참가자 ID에 해당하는 활성 Saga ID를 조회
	 */
	String findActiveSagaId(UUID competitionId, UUID participantId);
}
