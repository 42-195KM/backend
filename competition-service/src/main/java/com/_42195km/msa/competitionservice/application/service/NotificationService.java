package com._42195km.msa.competitionservice.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

	/**
	 * 대회 신청 완료 알림
	 * TODO : 알림 도메인 구현시 적용
	 * @param competitionId
	 * @param participantId
	 */
	public void sendApplicationCompletedNotification(UUID competitionId, UUID participantId) {
		try {
			log.info("Application completed notification sent: competitionId={}, participantId={}",
				competitionId, participantId);

		} catch (Exception e) {
			log.error("Failed to send application completed notification: {}", e.getMessage(), e);
		}
	}

	/**
	 * 대회 취소 알림
	 * TODO : 알림 도메인 구현시 적용
	 * @param competitionId
	 * @param participantId
	 * @param reason
	 */
	public void sendCancellationNotification(UUID competitionId, UUID participantId, String reason) {
		try {
			log.info("Cancellation notification sent: competitionId={}, participantId={}, reason={}",
				competitionId, participantId, reason);

		} catch (Exception e) {
			log.error("Failed to send cancellation notification: {}", e.getMessage(), e);
		}
	}

	/**
	 * 환불 완료 알림
	 * TODO : 알림 도메인 구현시 적용
	 * @param competitionId
	 * @param participantId
	 * @param refundId
	 */
	public void sendRefundCompletedNotification(UUID competitionId, UUID participantId, String refundId) {
		try {
			log.info("Refund completed notification sent: competitionId={}, participantId={}, refundId={}",
				competitionId, participantId, refundId);

		} catch (Exception e) {
			log.error("Failed to send refund completed notification: {}", e.getMessage(), e);
		}
	}
}