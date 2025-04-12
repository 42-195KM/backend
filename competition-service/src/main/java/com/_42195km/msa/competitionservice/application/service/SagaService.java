package com._42195km.msa.competitionservice.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com._42195km.msa.common.exception.CustomBusinessException;
import com._42195km.msa.competitionservice.application.exception.CompetitionServiceCode;
import com._42195km.msa.competitionservice.domain.model.SagaState;
import com._42195km.msa.competitionservice.domain.model.SagaStatus;
import com._42195km.msa.competitionservice.domain.model.SagaStep;
import com._42195km.msa.competitionservice.infrastructure.persistence.SagaStateRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SagaService {
	private final SagaStateRepository sagaStateRepository;
	private final CompetitionSagaOrchestrator sagaOrchestrator;
	private final CompetitionService competitionService;

	public String findOrCreateSagaId(UUID competitionId, UUID participantId) {

		String sagaId = sagaStateRepository.findActiveSagaId(competitionId, participantId);

		if (sagaId == null) {
			sagaId = sagaOrchestrator.startApplicationSaga(competitionId, participantId);
			log.info("Created new Saga with ID: {}", sagaId);
		} else {
			log.info("Found existing Saga with ID: {}", sagaId);
		}

		return sagaId;
	}

	// 단계별 진행
	public void processApplicationStep(String step, UUID competitionId, UUID participantId,
		Boolean termsAgreed, String souvenirSelection, String shippingAddress) {

		try {
			String sagaId = findOrCreateSagaId(competitionId, participantId);

			// 현재 Saga 상태 조회
			SagaState state = sagaStateRepository.getSagaState(sagaId);
			if (state == null) {
				throw new RuntimeException("Saga state not found for ID: " + sagaId);
			}

			// 단계에 따른 처리
			switch (step) {
				case "TERMS":
					if (termsAgreed == null) {
						throw new IllegalArgumentException("Terms agreement is required");
					}
					sagaOrchestrator.processTermsAgreement(sagaId, competitionId, participantId, termsAgreed);
					break;

				case "SOUVENIR":
					if (souvenirSelection == null) {
						throw new IllegalArgumentException("Souvenir selection is required");
					}
					sagaOrchestrator.processSouvenirSelection(sagaId, competitionId, participantId, souvenirSelection);
					break;

				case "SHIPPING":
					if (shippingAddress == null) {
						throw new IllegalArgumentException("Shipping address is required");
					}
					sagaOrchestrator.processShippingAddress(sagaId, competitionId, participantId, shippingAddress);
					break;

				default:
					throw new IllegalArgumentException("Unknown step: " + step);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_APPLY_FAIL);
		}
	}

	// 전체 프로세스 자동 실행 메서드
	public String processCompleteApplication(UUID competitionId, UUID participantId,
		Boolean termsAgreed, String souvenirSelection, String shippingAddress, String paymentMethod,
		String paymentStatus, String transactionId) {
		try {
			String sagaId = findOrCreateSagaId(competitionId, participantId);
			SagaState state = sagaStateRepository.getSagaState(sagaId);

			// 현재 진행 단계 확인 및 처리할 작업 결정
			String nextActionMsg = "";

			// 1. 약관 동의 단계 처리
			if (termsAgreed != null && !state.getCompletedSteps().contains(SagaStep.TERMS_AGREEMENT)) {
				sagaOrchestrator.processTermsAgreement(sagaId, competitionId, participantId, termsAgreed);
				nextActionMsg = "약관 동의가 완료되었습니다. 다음은 기념품을 선택해주세요.";
			}
			// 2. 기념품 선택 단계 처리
			else if (souvenirSelection != null && state.getCompletedSteps().contains(SagaStep.TERMS_AGREEMENT)
				&& !state.getCompletedSteps().contains(SagaStep.SOUVENIR_SELECTION)) {
				sagaOrchestrator.processSouvenirSelection(sagaId, competitionId, participantId, souvenirSelection);
				nextActionMsg = "기념품 선택이 완료되었습니다. 다음은 배송지를 입력해주세요.";
			}
			// 3. 배송지 입력 단계 처리
			else if (shippingAddress != null && state.getCompletedSteps().contains(SagaStep.SOUVENIR_SELECTION)
				&& !state.getCompletedSteps().contains(SagaStep.SHIPPING_ADDRESS)) {
				sagaOrchestrator.processShippingAddress(sagaId, competitionId, participantId, shippingAddress);
				nextActionMsg = "배송지 입력이 완료되었습니다. 다음은 결제를 진행해주세요.";
			}
			// 4. 결제 시작 단계 처리
			else if (paymentMethod != null && state.getCompletedSteps().contains(SagaStep.SHIPPING_ADDRESS)
				&& !state.getCompletedSteps().contains(SagaStep.PAYMENT_INITIATED)) {
				// 대회 가격 조회
				sagaOrchestrator.initiatePayment(sagaId, competitionId, participantId, paymentMethod);
				nextActionMsg = "결제가 시작되었습니다. 결제를 완료해주세요.";
			}
			// 5. 결제 완료 단계 처리
			else if (paymentStatus != null && transactionId != null && state.getCompletedSteps()
				.contains(SagaStep.PAYMENT_INITIATED)
				&& !state.getCompletedSteps().contains(SagaStep.PAYMENT_PROCESSED)) {
				Integer amount = competitionService.getCompetition(competitionId).getPrice();
				sagaOrchestrator.completePayment(
					sagaId,
					competitionId,
					participantId,
					amount,
					state.getPaymentMethod(), // 이전 단계에서 저장된 결제 방법 사용
					paymentStatus,
					transactionId
				);
				nextActionMsg = "결제가 완료되었습니다. 신청 자격 확인 중입니다.";
			}
			// 6. 이미 완료된 단계가 있는 경우 현재 상태 확인
			else {
				// 현재 상태에 따른 메시지 생성
				if (state.getStatus() == SagaStatus.COMPLETED) {
					nextActionMsg = "이미 대회 신청이 완료되었습니다.";
				} else if (state.getStatus() == SagaStatus.FAILED) {
					nextActionMsg = "대회 신청 처리 중 오류가 발생했습니다.";
				} else if (state.getCurrentStep() == SagaStep.TERMS_AGREEMENT) {
					nextActionMsg = "약관 동의가 필요합니다.";
				} else if (state.getCurrentStep() == SagaStep.SOUVENIR_SELECTION) {
					nextActionMsg = "기념품 선택이 필요합니다.";
				} else if (state.getCurrentStep() == SagaStep.SHIPPING_ADDRESS) {
					nextActionMsg = "배송지 입력이 필요합니다.";
				} else if (state.getCurrentStep() == SagaStep.PAYMENT_INITIATED) {
					nextActionMsg = "결제 정보 입력이 필요합니다.";
				} else if (state.getCurrentStep() == SagaStep.PAYMENT_PROCESSED) {
					nextActionMsg = "결제 완료 처리가 필요합니다.";
				} else if (state.getCurrentStep() == SagaStep.ELIGIBILITY_CHECK) {
					nextActionMsg = "신청 자격 확인 중입니다.";
				} else if (state.getCurrentStep() == SagaStep.PARTICIPATION_CONFIRMED) {
					nextActionMsg = "참가 확정 중입니다.";
				} else if (state.getCurrentStep() == SagaStep.NOTIFICATION_SENT) {
					nextActionMsg = "알림 발송 중입니다.";
				} else {
					nextActionMsg = "현재 처리 중인 단계: " + state.getCurrentStep();
				}
			}

			return nextActionMsg;
		} catch (Exception e) {
			log.error("saga service 중 오류 : {}", e.getMessage());
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_APPLY_FAIL);

		}
	}

	public String findActiveSagaId(UUID competitionId, UUID participantId) {
		String state = sagaStateRepository.findActiveSagaId(competitionId, participantId);
		if (state == null) {
			throw CustomBusinessException.from(CompetitionServiceCode.PARTICIPANT_GET_FAIL);
		}
		return state;
	}
}
