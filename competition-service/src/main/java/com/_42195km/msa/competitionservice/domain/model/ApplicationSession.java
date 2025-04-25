package com._42195km.msa.competitionservice.domain.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class ApplicationSession implements Serializable {
	private String sessionId;
	private UUID competitionId;
	private UUID participantId;

	private LocalDateTime startTime;
	private LocalDateTime termsAgreedTime;
	private LocalDateTime souvenirSelectedTime;
	private LocalDateTime shippingEnteredTime;
	private LocalDateTime paymentCompletedTime;
	private LocalDateTime completedTime;

	private boolean completed;
	private boolean failed;
	private String failReason;

	// 신청 시작 시 호출
	public static ApplicationSession start(UUID competitionId, UUID participantId) {
		ApplicationSession session = new ApplicationSession();
		session.sessionId = UUID.randomUUID().toString();
		session.competitionId = competitionId;
		session.participantId = participantId;
		session.startTime = LocalDateTime.now();
		return session;
	}

	// 약관 동의 단계 완료
	public void completeTermsAgreement() {
		this.termsAgreedTime = LocalDateTime.now();
	}

	// 기념품 선택 단계 완료
	public void completeSouvenirSelection() {
		this.souvenirSelectedTime = LocalDateTime.now();
	}

	// 배송지 입력 단계 완료
	public void completeShippingAddress() {
		this.shippingEnteredTime = LocalDateTime.now();
	}

	// 결제 단계 완료
	public void completePayment() {
		this.paymentCompletedTime = LocalDateTime.now();
	}

	// 전체 프로세스 완료
	public void complete() {
		this.completedTime = LocalDateTime.now();
		this.completed = true;
	}

	// 실패 처리
	public void fail(String reason) {
		this.failed = true;
		this.failReason = reason;
	}

	// 단계별 소요 시간 계산 메서드들
	public long getTermsStepTimeMillis() {
		if (startTime == null || termsAgreedTime == null) return 0;
		return java.time.Duration.between(startTime, termsAgreedTime).toMillis();
	}

	public long getSouvenirStepTimeMillis() {
		if (termsAgreedTime == null || souvenirSelectedTime == null) return 0;
		return java.time.Duration.between(termsAgreedTime, souvenirSelectedTime).toMillis();
	}

	public long getShippingStepTimeMillis() {
		if (souvenirSelectedTime == null || shippingEnteredTime == null) return 0;
		return java.time.Duration.between(souvenirSelectedTime, shippingEnteredTime).toMillis();
	}

	public long getPaymentStepTimeMillis() {
		if (shippingEnteredTime == null || paymentCompletedTime == null) return 0;
		return java.time.Duration.between(shippingEnteredTime, paymentCompletedTime).toMillis();
	}

	public long getTotalTimeMillis() {
		if (startTime == null || completedTime == null) return 0;
		return java.time.Duration.between(startTime, completedTime).toMillis();
	}
}
