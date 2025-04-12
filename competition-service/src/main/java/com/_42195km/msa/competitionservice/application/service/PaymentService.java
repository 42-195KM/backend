package com._42195km.msa.competitionservice.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com._42195km.msa.common.exception.CustomBusinessException;
import com._42195km.msa.competitionservice.application.exception.CompetitionServiceCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentService {

	/**
	 * 결제 요청 처리 : 간단히 UUID 생성하여 결제 ID로 반환하는 것으로 결제 처리
	 * @param competitionId
	 * @param participantId
	 * @param amount
	 * @param paymentMethod
	 * @return
	 */
	@Transactional
	public String processPayment(UUID competitionId, UUID participantId, Integer amount, String paymentMethod) {
		try {

			String paymentId = UUID.randomUUID().toString();

			log.info("Payment processed: competitionId={}, participantId={}, amount={}, method={}, paymentId={}",
				competitionId, participantId, amount, paymentMethod, paymentId);

			return paymentId;

		} catch (Exception e) {
			log.error("Payment processing failed: {}", e.getMessage(), e);
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_APPLY_FAIL);
		}
	}

	/**
	 * 결제 취소 처리 (보상 트랜잭션) : 로그만 남김
	 * @param paymentId
	 */
	@Transactional
	public void cancelPayment(String paymentId) {
		try {
			log.info("Payment cancelled: paymentId={}", paymentId);

		} catch (Exception e) {
			log.error("Payment cancellation failed: {}", e.getMessage(), e);
			throw CustomBusinessException.from(CompetitionServiceCode.PARTICIPANT_CANCEL_FAIL);
		}
	}

	/**
	 * 환불 처리 : 로그만 남김
	 * @param paymentId
	 * @param amount
	 * @return
	 */
	@Transactional
	public String processRefund(String paymentId, Integer amount) {
		try {
			String refundId = UUID.randomUUID().toString();

			log.info("Refund processed: paymentId={}, amount={}, refundId={}",
				paymentId, amount, refundId);

			return refundId;

		} catch (Exception e) {
			log.error("Refund processing failed: {}", e.getMessage(), e);
			throw CustomBusinessException.from(CompetitionServiceCode.PARTICIPANT_CANCEL_FAIL);
		}
	}
}
