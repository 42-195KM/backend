package com._42195km.msa.competitionservice.application.event;

import java.util.UUID;

import com._42195km.msa.competitionservice.domain.model.SagaStep;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public class PaymentSagaEvent extends SagaEvent {
	private Integer amount;
	private String paymentMethod;
	private String paymentStatus;
	private String paymentTransactionId;

	public PaymentSagaEvent(String sagaId, UUID competitionId, UUID participantId,
		SagaStep step, boolean compensation,
		Integer amount, String paymentMethod, String paymentStatus, String paymentTransactionId) {
		super(sagaId, competitionId, participantId, step, compensation);
		this.amount = amount;
		this.paymentMethod = paymentMethod;
		this.paymentStatus = paymentStatus;
		this.paymentTransactionId = paymentTransactionId;
	}

	// 결제 시작 이벤트 생성 헬퍼 메서드
	public static PaymentSagaEvent createPaymentInitiatedEvent(String sagaId, UUID competitionId, UUID participantId, Integer amount, String paymentMethod, boolean compensation) {
		return new PaymentSagaEvent(
			sagaId, competitionId, participantId,
			SagaStep.PAYMENT_INITIATED, compensation,
			amount, paymentMethod, "INITIATED", null
		);
	}

	// 결제 완료 이벤트 생성 헬퍼 메서드
	public static PaymentSagaEvent createPaymentProcessedEvent(String sagaId, UUID competitionId, UUID participantId, Integer amount, String paymentMethod, String paymentStatus, String paymentTransactionId, boolean compensation) {
		return new PaymentSagaEvent(
			sagaId, competitionId, participantId,
			SagaStep.PAYMENT_PROCESSED, compensation,
			amount, paymentMethod, paymentStatus, paymentTransactionId
		);
	}
}
