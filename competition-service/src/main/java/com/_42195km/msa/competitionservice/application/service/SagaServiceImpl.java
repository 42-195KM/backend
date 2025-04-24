package com._42195km.msa.competitionservice.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com._42195km.msa.common.exception.CustomBusinessException;
import com._42195km.msa.competitionservice.application.dto.CompleteAppDto;
import com._42195km.msa.competitionservice.application.exception.CompetitionServiceCode;
import com._42195km.msa.competitionservice.domain.model.ApplicationStep;
import com._42195km.msa.competitionservice.domain.model.Competition;
import com._42195km.msa.competitionservice.domain.model.CompetitionParticipantMapping;
import com._42195km.msa.competitionservice.domain.model.Participant;
import com._42195km.msa.competitionservice.domain.model.PaymentInfo;
import com._42195km.msa.competitionservice.domain.model.SagaState;
import com._42195km.msa.competitionservice.domain.model.SagaStatus;
import com._42195km.msa.competitionservice.infrastructure.messaging.EventPublisher;
import com._42195km.msa.competitionservice.infrastructure.messaging.SagaOrchestratorImpl;
import com._42195km.msa.competitionservice.infrastructure.persistence.CompetitionRepositoryImpl;
import com._42195km.msa.competitionservice.infrastructure.persistence.ParticipantRepositoryImpl;
import com._42195km.msa.competitionservice.infrastructure.persistence.SagaStateRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SagaServiceImpl implements SagaService {

	private final SagaStateRepository sagaStateRepository;
	private final SagaOrchestratorImpl sagaOrchestrator;
	private final CompetitionRepositoryImpl competitionRepository;
	private final ParticipantRepositoryImpl participantRepository;
	private final EventPublisher eventPublisher;

	@Override
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

	// 전체 프로세스 메서드
	@Override
	public String processCompleteApplication(CompleteAppDto requestDto) {
		try {
			// 1. 대회 조회
			Competition competition = competitionRepository.findById(requestDto.getCompetitionId());

			// 2. 현재 사가 상태 조회 또는 생성
			String sagaId = findOrCreateSagaId(requestDto.getCompetitionId(), requestDto.getParticipantId());
			SagaState sagaState = sagaStateRepository.getSagaState(sagaId);

			// 3. 참가자 조회 또는 생성
			Participant participant = participantRepository.findByParticipantId(requestDto.getParticipantId());
			if (participant == null) {
				participant = new Participant(requestDto.getParticipantId());
				participantRepository.save(participant);
			}

			// 4. 현재 단계 결정 및 데이터 준비
			ApplicationStep currentStep;
			Object stepData = null;

			CompetitionParticipantMapping mapping = competition.findParticipantMapping(requestDto.getParticipantId())
				.orElse(null);

			if (mapping == null) {
				currentStep = ApplicationStep.TERMS_AGREEMENT;
				stepData = requestDto.getTermsAgreed();
			} else {
				currentStep = mapping.getApplicationStep();

				// 단계별 데이터 설정
				switch (currentStep) {
					case TERMS_AGREEMENT:
						stepData = requestDto.getTermsAgreed();
						break;
					case SOUVENIR_SELECTION:
						stepData = requestDto.getSouvenirSelection();
						break;
					case SHIPPING_ADDRESS:
						stepData = requestDto.getShippingAddress();
						break;
					case PAYMENT_PENDING:
						if (requestDto.getPaymentMethod() != null && requestDto.getPaymentStatus() != null) {
							stepData = new PaymentInfo(
								requestDto.getPaymentMethod(),
								requestDto.getPaymentStatus(),
								requestDto.getTransactionId()
							);
						}
						break;
				}
			}

			// 해당 단계 데이터가 없으면 현재 단계 메시지만 반환
			if (stepData == null) {
				// 현재 상태에 따른 메시지 생성
				return getNextStepMessage(currentStep);
			}

			// 5. 도메인 모델에서 단계 처리 (이벤트도 발행)
			competition.processApplicationStep(
				requestDto.getParticipantId(),
				participant,
				currentStep,
				stepData,
				eventPublisher // 이벤트 발행자(publisher) 역할
			);

			// 6. 변경사항 저장
			competitionRepository.save(competition);

			// 7. 업데이트된 매핑 조회 및 다음 단계 메시지 반환
			mapping = competition.findParticipantMapping(requestDto.getParticipantId())
				.orElseThrow(() -> new RuntimeException("매핑을 찾을 수 없습니다"));

			return getNextStepMessage(mapping.getApplicationStep());

		} catch (CustomBusinessException e) {
			log.error("대회 신청 처리 중 비즈니스 오류 발생: {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("대회 신청 처리 중 오류 발생: {}", e.getMessage());
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_APPLY_FAIL);
		}
	}

	private String getStatusMessage(SagaState state) {
		if (state.getStatus() == SagaStatus.COMPLETED) {
			return "이미 대회 신청이 완료되었습니다.";
		} else if (state.getStatus() == SagaStatus.FAILED) {
			return "대회 신청 처리 중 오류가 발생했습니다.";
		} else {
			return switch (state.getCurrentStep()) {
				case TERMS_AGREEMENT -> "약관 동의가 필요합니다.";
				case SOUVENIR_SELECTION -> "기념품 선택이 필요합니다.";
				case SHIPPING_ADDRESS -> "배송지 입력이 필요합니다.";
				case PAYMENT_INITIATED -> "결제 정보 입력이 필요합니다.";
				case PAYMENT_PROCESSED -> "결제 완료 처리가 필요합니다.";
				case ELIGIBILITY_CHECK -> "신청 자격 확인 중입니다.";
				case PARTICIPATION_CONFIRMED -> "참가 확정 중입니다.";
				case NOTIFICATION_SENT -> "알림 발송 중입니다.";
				default -> "현재 처리 중인 단계: " + state.getCurrentStep();
			};
		}
	}

	public String findActiveSagaId(UUID competitionId, UUID participantId) {
		String state = sagaStateRepository.findActiveSagaId(competitionId, participantId);
		if (state == null) {
			throw CustomBusinessException.from(CompetitionServiceCode.PARTICIPANT_GET_FAIL);
		}
		return state;
	}

	private String getNextStepMessage(ApplicationStep step) {
		switch (step) {
			case TERMS_AGREEMENT:
				return "약관 동의가 필요합니다.";
			case SOUVENIR_SELECTION:
				return "기념품 선택이 필요합니다.";
			case SHIPPING_ADDRESS:
				return "배송지 입력이 필요합니다.";
			case PAYMENT_PENDING:
				return "결제 진행이 필요합니다.";
			case PAYMENT_COMPLETED:
				return "결제가 완료되었습니다. 신청 자격 확인 중입니다.";
			case PARTICIPATION_CONFIRMED:
				return "대회 참가가 확정되었습니다.";
			default:
				return "알 수 없는 단계입니다.";
		}
	}
}
