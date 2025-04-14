package com._42195km.msa.competitionservice.application.event;

import java.util.UUID;

import com._42195km.msa.competitionservice.domain.model.SagaStep;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
	@JsonSubTypes.Type(value = ApplicationSagaEvent.class, name = "APPLICATION"),
	@JsonSubTypes.Type(value = PaymentSagaEvent.class, name = "PAYMENT"),
	@JsonSubTypes.Type(value = CancellationSagaEvent.class, name = "CANCELLATION")
})
public class SagaEvent {
	private String sagaId;
	private UUID competitionId;
	private UUID participantId;
	private SagaStep step;
	private boolean compensation; // 보상 트랜젝션 여부
}
