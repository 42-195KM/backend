package com._42195km.msa.competitionservice.domain.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SagaState implements Serializable {
	private String sagaId;
	private String sagaType;
	private UUID competitionId;
	private UUID participantId;
	private SagaStatus status;
	private SagaStep currentStep;
	private List<SagaStep> completedSteps = new ArrayList<>();

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	// 대회 신청 관련 데이터
	private Boolean termsAgreed;
	private String souvenirSelection;
	private String shippingAddress;

	// 결제 관련 데이터
	private Integer amount;
	private String paymentMethod;
	private String paymentStatus;
	private String paymentTransactionId;

	// 참가 가능 확인 관련
	private String eligibilityStatus;
	private String eligibilityReason;

	// 취소 관련 데이터
	private String cancellationReason;
	private Boolean refundRequired;
	private String refundStatus;

	public SagaState(String sagaType, UUID competitionId, UUID participantId) {
		this.sagaId = UUID.randomUUID().toString();
		this.sagaType = sagaType;
		this.competitionId = competitionId;
		this.participantId = participantId;
		this.status = SagaStatus.STARTED;
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	private void updateModifiedTime() {
		this.updatedAt = LocalDateTime.now();
	}

	public void markStepAsCompleted(SagaStep step) {
		this.completedSteps.add(step);
		this.currentStep = null;
		updateModifiedTime();
	}

	public void setNextStep(SagaStep step) {
		this.currentStep = step;
		updateModifiedTime();
	}

	public void markAsCompleted() {
		this.status = SagaStatus.COMPLETED;
		updateModifiedTime();
	}

	public void markAsFailed() {
		this.status = SagaStatus.FAILED;
		updateModifiedTime();
	}

	public void startCompensation() {
		this.status = SagaStatus.COMPENSATING;
		updateModifiedTime();
	}

	public void completeCompensation() {
		this.status = SagaStatus.COMPENSATED;
		updateModifiedTime();
	}

	public void updateTermsAgreed(Boolean termsAgreed) {
		this.termsAgreed = termsAgreed;
		updateModifiedTime();
	}

	public void updateSouvenirSelection(String souvenirSelection) {
		this.souvenirSelection = souvenirSelection;
		updateModifiedTime();
	}

	public void updateShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
		updateModifiedTime();
	}

	public void updateAmount(Integer amount) {
		this.amount = amount;
		updateModifiedTime();
	}

	public void updatePaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
		updateModifiedTime();
	}

	public void updatePaymentStatus(String status) {
		this.paymentStatus = status;
		updateModifiedTime();
	}

	public void updatePaymentTransactionId(String transactionId) {
		this.paymentTransactionId = transactionId;
		updateModifiedTime();
	}

	public void updateCancellationReason(String reason) {
		this.cancellationReason = reason;
		updateModifiedTime();
	}

	public void updateRefundRequired(boolean refundRequired) {
		this.refundRequired = refundRequired;
		updateModifiedTime();
	}

	public void updateRefundStatus(String refundStatus) {
		this.refundStatus = refundStatus;
		updateModifiedTime();
	}

	public void updateEligibilityStatus(String eligibilityStatus) {
		this.eligibilityStatus = eligibilityStatus;
		this.updatedAt = LocalDateTime.now();
	}

	public void updateEligibilityReason(String eligibilityReason) {
		this.eligibilityReason = eligibilityReason;
		this.updatedAt = LocalDateTime.now();
	}
}
