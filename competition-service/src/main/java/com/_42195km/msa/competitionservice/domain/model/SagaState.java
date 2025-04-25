package com._42195km.msa.competitionservice.domain.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SagaState implements Serializable {
	private static final long serialVersionUID = 1L;

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
		log.debug("Created new SagaState: {}, type: {}, status: {}", this.sagaId, sagaType, this.status);
	}

	private void updateModifiedTime() {
		this.updatedAt = LocalDateTime.now();
	}

	public void markStepAsCompleted(SagaStep step) {
		log.debug("Marking step as completed: {} for saga: {}", step, this.sagaId);
		if (!this.completedSteps.contains(step)) {
			this.completedSteps.add(step);
		}
		this.currentStep = null;
		updateModifiedTime();
	}

	public void setNextStep(SagaStep step) {
		log.debug("Setting next step: {} for saga: {}", step, this.sagaId);
		this.currentStep = step;
		this.status = SagaStatus.IN_PROGRESS;
		updateModifiedTime();
	}

	public void markAsCompleted() {
		log.debug("Marking saga as completed: {}", this.sagaId);
		this.status = SagaStatus.COMPLETED;
		updateModifiedTime();
	}

	public void markAsFailed() {
		log.debug("Marking saga as failed: {}", this.sagaId);
		this.status = SagaStatus.FAILED;
		updateModifiedTime();
	}

	public void startCompensation() {
		log.debug("Starting compensation for saga: {}", this.sagaId);
		this.status = SagaStatus.COMPENSATING;
		updateModifiedTime();
	}

	public void completeCompensation() {
		log.debug("Compensation completed for saga: {}", this.sagaId);
		this.status = SagaStatus.COMPENSATED;
		updateModifiedTime();
	}

	public void updateTermsAgreed(Boolean termsAgreed) {
		log.debug("Updating terms agreed: {} for saga: {}", termsAgreed, this.sagaId);
		this.termsAgreed = termsAgreed;
		updateModifiedTime();
	}

	public void updateSouvenirSelection(String souvenirSelection) {
		log.debug("Updating souvenir selection: {} for saga: {}", souvenirSelection, this.sagaId);
		this.souvenirSelection = souvenirSelection;
		updateModifiedTime();
	}

	public void updateShippingAddress(String shippingAddress) {
		log.debug("Updating shipping address: {} for saga: {}", shippingAddress, this.sagaId);
		this.shippingAddress = shippingAddress;
		updateModifiedTime();
	}

	public void updateAmount(Integer amount) {
		log.debug("Updating amount: {} for saga: {}", amount, this.sagaId);
		this.amount = amount;
		updateModifiedTime();
	}

	public void updatePaymentMethod(String paymentMethod) {
		log.debug("Updating payment method: {} for saga: {}", paymentMethod, this.sagaId);
		this.paymentMethod = paymentMethod;
		updateModifiedTime();
	}

	public void updatePaymentStatus(String status) {
		log.debug("Updating payment status: {} for saga: {}", status, this.sagaId);
		this.paymentStatus = status;
		updateModifiedTime();
	}

	public void updatePaymentTransactionId(String transactionId) {
		log.debug("Updating transaction ID: {} for saga: {}", transactionId, this.sagaId);
		this.paymentTransactionId = transactionId;
		updateModifiedTime();
	}

	public void updateCancellationReason(String reason) {
		log.debug("Updating cancellation reason: {} for saga: {}", reason, this.sagaId);
		this.cancellationReason = reason;
		updateModifiedTime();
	}

	public void updateRefundRequired(boolean refundRequired) {
		log.debug("Updating refund required: {} for saga: {}", refundRequired, this.sagaId);
		this.refundRequired = refundRequired;
		updateModifiedTime();
	}

	public void updateRefundStatus(String refundStatus) {
		log.debug("Updating refund status: {} for saga: {}", refundStatus, this.sagaId);
		this.refundStatus = refundStatus;
		updateModifiedTime();
	}

	public void updateEligibilityStatus(String eligibilityStatus) {
		log.debug("Updating eligibility status: {} for saga: {}", eligibilityStatus, this.sagaId);
		this.eligibilityStatus = eligibilityStatus;
		this.updatedAt = LocalDateTime.now();
	}

	public void updateEligibilityReason(String eligibilityReason) {
		log.debug("Updating eligibility reason: {} for saga: {}", eligibilityReason, this.sagaId);
		this.eligibilityReason = eligibilityReason;
		this.updatedAt = LocalDateTime.now();
	}
}