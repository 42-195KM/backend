package com._42195km.msa.competitionservice.application.event;

import com._42195km.msa.competitionservice.domain.model.SagaStep;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ApplicationSagaEvent extends SagaEvent{
	private Boolean termsAgreed;
	private String souvenirSelection;
	private String shippingAddress;

	public ApplicationSagaEvent(String sagaId, UUID competitionId, UUID participantId,
		SagaStep step, boolean compensation,
		Boolean termsAgreed, String souvenirSelection, String shippingAddress) {
		super(sagaId, competitionId, participantId, step, compensation);
		this.termsAgreed = termsAgreed;
		this.souvenirSelection = souvenirSelection;
		this.shippingAddress = shippingAddress;
	}

	// 약관 동의 이벤트 생성 헬퍼 메서드
	public static ApplicationSagaEvent createTermsEvent(String sagaId, UUID competitionId, UUID participantId, Boolean termsAgreed, boolean compensation) {
		return new ApplicationSagaEvent(
			sagaId, competitionId, participantId,
			SagaStep.TERMS_AGREEMENT, compensation,
			termsAgreed, null, null
		);
	}

	// 기념품 선택 이벤트 생성 헬퍼 메서드
	public static ApplicationSagaEvent createSouvenirEvent(String sagaId, UUID competitionId, UUID participantId, String souvenirSelection, boolean compensation) {
		return new ApplicationSagaEvent(
			sagaId, competitionId, participantId,
			SagaStep.SOUVENIR_SELECTION, compensation,
			null, souvenirSelection, null
		);
	}

	// 배송지 입력 이벤트 생성 헬퍼 메서드
	public static ApplicationSagaEvent createShippingEvent(String sagaId, UUID competitionId, UUID participantId, String shippingAddress, boolean compensation) {
		return new ApplicationSagaEvent(
			sagaId, competitionId, participantId,
			SagaStep.SHIPPING_ADDRESS, compensation,
			null, null, shippingAddress
		);
	}
}
