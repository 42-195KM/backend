package com._42195km.msa.competitionservice.application.facade;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com._42195km.msa.competitionservice.application.dto.CompleteAppDto;
import com._42195km.msa.competitionservice.application.dto.request.CreateCompetitionCommandDto;
import com._42195km.msa.competitionservice.application.dto.request.UpdateCompetitionCommandDto;
import com._42195km.msa.competitionservice.application.dto.response.CompetitionAppResponseDto;
import com._42195km.msa.competitionservice.application.dto.response.ParticipantAppResponseDto;
import com._42195km.msa.competitionservice.application.dto.response.SearchParticipantAppResponseDto;
import com._42195km.msa.competitionservice.application.service.CompetitionService;
import com._42195km.msa.competitionservice.application.service.ParticipantService;
import com._42195km.msa.competitionservice.application.service.SagaService;
import com._42195km.msa.competitionservice.domain.model.ApplicationSession;
import com._42195km.msa.competitionservice.domain.model.ApplicationStep;
import com._42195km.msa.competitionservice.domain.repository.ApplicationSessionRepository;
import com._42195km.msa.competitionservice.presentation.dto.request.CancelParticipantRequestDto;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CompetitionApplicationFacade {
	private final CompetitionService competitionService;
	private final ParticipantService participantService;
	private final SagaService sagaService;

	private final ApplicationSessionRepository sessionRepository; // 모니터링을 위한 세션 추가

	private final Counter applicationStartCounter;
	private final Counter applicationCompleteCounter;
	private final Counter applicationFailCounter;
	private final Counter termsAgreementCounter;
	private final Counter souvenirSelectionCounter;
	private final Counter shippingAddressCounter;
	private final Counter paymentCounter;
	private final Timer applicationTotalTimeTimer;
	private final Timer termsStepTimeTimer;
	private final Timer souvenirStepTimeTimer;
	private final Timer shippingStepTimeTimer;
	private final Timer paymentStepTimeTimer;

	public CompetitionApplicationFacade(
		CompetitionService competitionService,
		ParticipantService participantService,
		SagaService sagaService,
		ApplicationSessionRepository sessionRepository,
		@Qualifier("applicationStartCounter") Counter applicationStartCounter,
		@Qualifier("applicationCompleteCounter") Counter applicationCompleteCounter,
		@Qualifier("applicationFailCounter") Counter applicationFailCounter,
		@Qualifier("termsAgreementCounter") Counter termsAgreementCounter,
		@Qualifier("souvenirSelectionCounter") Counter souvenirSelectionCounter,
		@Qualifier("shippingAddressCounter") Counter shippingAddressCounter,
		@Qualifier("paymentCounter") Counter paymentCounter,
		@Qualifier("applicationTotalTimeTimer") Timer applicationTotalTimeTimer,
		@Qualifier("termsStepTimeTimer") Timer termsStepTimeTimer,
		@Qualifier("souvenirStepTimeTimer") Timer souvenirStepTimeTimer,
		@Qualifier("shippingStepTimeTimer") Timer shippingStepTimeTimer,
		@Qualifier("paymentStepTimeTimer") Timer paymentStepTimeTimer
	) {
		this.competitionService = competitionService;
		this.participantService = participantService;
		this.sagaService = sagaService;
		this.sessionRepository = sessionRepository;
		this.applicationStartCounter = applicationStartCounter;
		this.applicationCompleteCounter = applicationCompleteCounter;
		this.applicationFailCounter = applicationFailCounter;
		this.termsAgreementCounter = termsAgreementCounter;
		this.souvenirSelectionCounter = souvenirSelectionCounter;
		this.shippingAddressCounter = shippingAddressCounter;
		this.paymentCounter = paymentCounter;
		this.applicationTotalTimeTimer = applicationTotalTimeTimer;
		this.termsStepTimeTimer = termsStepTimeTimer;
		this.souvenirStepTimeTimer = souvenirStepTimeTimer;
		this.shippingStepTimeTimer = shippingStepTimeTimer;
		this.paymentStepTimeTimer = paymentStepTimeTimer;
	}

	public void createCompetition(CreateCompetitionCommandDto command) {
		competitionService.createCompetition(command);
	}

	public Page<CompetitionAppResponseDto> getCompetitions(Pageable pageable) {
		return competitionService.getCompetitions(pageable);
	}

	public Page<CompetitionAppResponseDto> searchCompetitions(String keyword, Pageable pageable) {
		return competitionService.searchCompetition(keyword, pageable);
	}

	public CompetitionAppResponseDto getCompetition(UUID competitionId) {
		return competitionService.getCompetition(competitionId);
	}

	public Page<CompetitionAppResponseDto> getHostCompetitions(UUID userId, Pageable pageable) {
		return competitionService.getHostCompetition(userId, pageable);
	}

	public void updateCompetition(UUID competitionId, UpdateCompetitionCommandDto commandDto) {
		competitionService.updateCompetition(competitionId, commandDto);
	}

	public void deleteCompetition(UUID competitionId) {
		competitionService.deleteCompetition(competitionId);
	}

	// 대회 신청 관련 기능들 //

	/**
	 * 대회 신청 프로세스
	 */
	public String applyForCompetition(CompleteAppDto appDto) {
		// 세션 조회 또는 생성
		ApplicationSession session = sessionRepository.findByCompetitionAndParticipant(
			appDto.getCompetitionId(), appDto.getParticipantId());

		if (session == null) {
			// 새로운 신청 세션 시작
			session = ApplicationSession.start(appDto.getCompetitionId(), appDto.getParticipantId());
			applicationStartCounter.increment();
			log.info("새로운 대회 신청 세션 시작: competitionId={}, participantId={}, sessionId={}",
				appDto.getCompetitionId(), appDto.getParticipantId(), session.getSessionId());
		}

		try {
			// 현재 단계 결정
			ApplicationStep currentStep = determineCurrentStep(appDto);

			// 서비스 호출하여 실제 처리
			String response = sagaService.processCompleteApplication(appDto);

			// 결과에 따라 세션 업데이트 및 메트릭 기록
			updateSessionAndMetrics(session, currentStep, appDto, response);

			// 세션 저장
			sessionRepository.saveSession(session);

			return response;
		} catch (Exception e) {
			// 실패 처리
			session.fail(e.getMessage());
			sessionRepository.saveSession(session);
			applicationFailCounter.increment();
			log.error("대회 신청 처리 실패: sessionId={}, error={}", session.getSessionId(), e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * 대회 신청 상태를 조회.
	 */
	public String getApplicationStatus(UUID competitionId, UUID participantId) {
		return sagaService.findActiveSagaId(competitionId, participantId);
	}

	/**
	 * 대회 추첨을 실행.
	 */
	public void drawCompetition(UUID competitionId) {
		competitionService.drawCompetition(competitionId);
	}

	// 참가자 관리 관련 기능들 //

	public Page<ParticipantAppResponseDto> getParticipants(Pageable pageable, UUID competitionId) {
		return participantService.getParticipants(pageable, competitionId);
	}

	public Page<SearchParticipantAppResponseDto> searchParticipants(String keyword, String searchType, Pageable pageable) {
		return participantService.searchParticipants(keyword, searchType, pageable);
	}

	public Page<SearchParticipantAppResponseDto> getParticipant(String keyword, Pageable pageable) {
		return participantService.getParticipant(keyword, pageable);
	}

	public void cancelParticipantByCompany(CancelParticipantRequestDto requestDto) {
		participantService.cancelParticipantByCompany(requestDto);
	}

	public void cancelParticipant(CancelParticipantRequestDto requestDto) {
		participantService.cancelParticipant(requestDto);
	}

	// 현재 단계 결정 메서드
	private ApplicationStep determineCurrentStep(CompleteAppDto appDto) {
		if (appDto.getTermsAgreed() != null) return ApplicationStep.TERMS_AGREEMENT;
		if (appDto.getSouvenirSelection() != null) return ApplicationStep.SOUVENIR_SELECTION;
		if (appDto.getShippingAddress() != null) return ApplicationStep.SHIPPING_ADDRESS;
		if (appDto.getPaymentMethod() != null) return ApplicationStep.PAYMENT_PENDING;
		if (appDto.getPaymentStatus() != null && appDto.getTransactionId() != null)
			return ApplicationStep.PAYMENT_COMPLETED;
		return ApplicationStep.TERMS_AGREEMENT;
	}

	// 세션 업데이트 및 메트릭 기록 메서드
	private void updateSessionAndMetrics(ApplicationSession session, ApplicationStep currentStep,
		CompleteAppDto appDto, String response) {
		switch (currentStep) {
			case TERMS_AGREEMENT:
				if (appDto.getTermsAgreed() != null && appDto.getTermsAgreed()) {
					session.completeTermsAgreement();
					termsAgreementCounter.increment();
					termsStepTimeTimer.record(session.getTermsStepTimeMillis(), TimeUnit.MILLISECONDS);
					log.info("약관 동의 단계 완료: sessionId={}, 소요시간={}ms",
						session.getSessionId(), session.getTermsStepTimeMillis());
				}
				break;

			case SOUVENIR_SELECTION:
				if (appDto.getSouvenirSelection() != null) {
					session.completeSouvenirSelection();
					souvenirSelectionCounter.increment();
					souvenirStepTimeTimer.record(session.getSouvenirStepTimeMillis(), TimeUnit.MILLISECONDS);
					log.info("기념품 선택 단계 완료: sessionId={}, 소요시간={}ms",
						session.getSessionId(), session.getSouvenirStepTimeMillis());
				}
				break;

			case SHIPPING_ADDRESS:
				if (appDto.getShippingAddress() != null) {
					session.completeShippingAddress();
					shippingAddressCounter.increment();
					shippingStepTimeTimer.record(session.getShippingStepTimeMillis(), TimeUnit.MILLISECONDS);
					log.info("배송지 입력 단계 완료: sessionId={}, 소요시간={}ms",
						session.getSessionId(), session.getShippingStepTimeMillis());
				}
				break;

			case PAYMENT_COMPLETED:
				if (appDto.getPaymentStatus() != null && "SUCCESS".equals(appDto.getPaymentStatus())) {
					session.completePayment();
					paymentCounter.increment();
					paymentStepTimeTimer.record(session.getPaymentStepTimeMillis(), TimeUnit.MILLISECONDS);

					// 신청 완료 처리 - 결제 성공 시 전체 프로세스가 완료됨
					session.complete();
					applicationCompleteCounter.increment();
					applicationTotalTimeTimer.record(session.getTotalTimeMillis(), TimeUnit.MILLISECONDS);

					log.info("대회 신청 프로세스 완료: sessionId={}, 총 소요시간={}ms",
						session.getSessionId(), session.getTotalTimeMillis());
				}
				break;

			default:
				break;
		}
	}
}
