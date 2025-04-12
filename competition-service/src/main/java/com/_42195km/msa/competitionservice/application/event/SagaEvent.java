package com._42195km.msa.competitionservice.application.event;

import java.util.UUID;

import com._42195km.msa.competitionservice.domain.model.SagaStep;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SagaEvent {
	private String sagaId;
	private UUID competitionId;
	private UUID participantId;
	private SagaStep step;
	private boolean compensation; // 보상 트랜젝션 여부
}
