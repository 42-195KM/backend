package com._42195km.msa.competitionservice.application.event;

import com._42195km.msa.competitionservice.domain.model.SagaStep;
import java.util.UUID;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public class CancellationSagaEvent extends SagaEvent{
	private String cancellationReason;
	private Boolean refundRequired;
	private String refundStatus;

	public CancellationSagaEvent(String sagaId, UUID competitionId, UUID participantId,
		SagaStep step, boolean compensation,
		String cancellationReason, Boolean refundRequired, String refundStatus) {
		super(sagaId, competitionId, participantId, step, compensation);
		this.cancellationReason = cancellationReason;
		this.refundRequired = refundRequired;
		this.refundStatus = refundStatus;
	}

	// 취소 요청 이벤트 생성 헬퍼 메서드
	public static CancellationSagaEvent createCancellationRequestedEvent(String sagaId, UUID competitionId, UUID participantId, String cancellationReason, Boolean refundRequired, boolean compensation) {
		return new CancellationSagaEvent(
			sagaId, competitionId, participantId,
			SagaStep.CANCELLATION_REQUESTED, compensation,
			cancellationReason, refundRequired, null
		);
	}

	// 환불 시작 이벤트 생성 헬퍼 메서드
	public static CancellationSagaEvent createRefundInitiatedEvent(String sagaId, UUID competitionId, UUID participantId, String cancellationReason, Boolean refundRequired, boolean compensation) {
		return new CancellationSagaEvent(
			sagaId, competitionId, participantId,
			SagaStep.REFUND_INITIATED, compensation,
			cancellationReason, refundRequired, "INITIATED"
		);
	}

	// 환불 완료 이벤트 생성 헬퍼 메서드
	public static CancellationSagaEvent createRefundProcessedEvent(String sagaId, UUID competitionId, UUID participantId, String cancellationReason, Boolean refundRequired, String refundStatus, boolean compensation) {
		return new CancellationSagaEvent(
			sagaId, competitionId, participantId,
			SagaStep.REFUND_PROCESSED, compensation,
			cancellationReason, refundRequired, refundStatus
		);
	}
}
