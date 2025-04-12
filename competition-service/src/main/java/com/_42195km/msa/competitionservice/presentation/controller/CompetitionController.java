package com._42195km.msa.competitionservice.presentation.controller;

import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._42195km.msa.common.api.ApiResponse;
import com._42195km.msa.competitionservice.application.dto.AppStepDto;
import com._42195km.msa.competitionservice.application.dto.CompleteAppDto;
import com._42195km.msa.competitionservice.application.dto.response.CompetitionAppResponseDto;
import com._42195km.msa.competitionservice.application.event.ApplicationStep;
import com._42195km.msa.competitionservice.application.event.CompetitionApplicationEvent;
import com._42195km.msa.competitionservice.application.exception.CompetitionServiceCode;
import com._42195km.msa.competitionservice.application.mapper.CompetitionMapper;
import com._42195km.msa.competitionservice.application.service.CompetitionSagaOrchestrator;
import com._42195km.msa.competitionservice.application.service.CompetitionService;
import com._42195km.msa.competitionservice.application.service.SagaService;
import com._42195km.msa.competitionservice.domain.model.Competition;
import com._42195km.msa.competitionservice.infrastructure.messaging.CompetitionApplicationProducer;
import com._42195km.msa.competitionservice.presentation.dto.request.ApplyCompetitionRequestDto;
import com._42195km.msa.competitionservice.presentation.dto.request.CancelParticipantRequestDto;
import com._42195km.msa.competitionservice.presentation.dto.request.CreateCompetitionRequestDto;
import com._42195km.msa.competitionservice.presentation.dto.request.GetRequestDto;
import com._42195km.msa.competitionservice.presentation.dto.request.PaymentRequestDto;
import com._42195km.msa.competitionservice.presentation.dto.request.SearchRequestDto;
import com._42195km.msa.competitionservice.presentation.dto.request.ShippingAddressRequestDto;
import com._42195km.msa.competitionservice.presentation.dto.request.SouvenirSelectionRequestDto;
import com._42195km.msa.competitionservice.presentation.dto.request.TermsAgreementRequestDto;
import com._42195km.msa.competitionservice.presentation.dto.request.UpdateCompetitionRequestDto;
import com._42195km.msa.competitionservice.presentation.dto.response.CompetitionResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/competitions")
@RequiredArgsConstructor
public class CompetitionController {

	private final CompetitionService competitionService;
	private final SagaService sagaService;

	private final CompetitionMapper competitionMapper;
	private final CompetitionSagaOrchestrator sagaOrchestrator;

	private final CompetitionApplicationProducer applicationProducer;

	@PostMapping("/")
	@Operation(summary = "대회 생성")
	public ResponseEntity<?> createCompetition(@RequestBody CreateCompetitionRequestDto requestDto) {
		competitionService.createCompetition(requestDto.toCommandDto());
		return ResponseEntity.ok(new ApiResponse<>(
			CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getCode(),
			"대회 생성에 성공했습니다.",
			CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@GetMapping("/")
	@Operation(summary = "대회 전체 조회")
	public ResponseEntity<?> getAllCompetitions(@ModelAttribute @Valid GetRequestDto requestDto) {
		Page<CompetitionAppResponseDto> competitions = competitionService.getCompetitions(requestDto.toPageable());
		Page<CompetitionResponseDto> presentationCompetitions = competitionMapper.toPresentationDtoPage(competitions);
		return ResponseEntity.ok(new ApiResponse<>(CompetitionServiceCode.COMPETITION_GET_SUCCESS.getCode(),
			presentationCompetitions,
			CompetitionServiceCode.COMPETITION_GET_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@GetMapping("/search")
	@Operation(summary = "대회 검색")
	public ResponseEntity<?> searchCompetitions(@ParameterObject SearchRequestDto requstDto) {
		Page<CompetitionAppResponseDto> competition = competitionService.searchCompetition(requstDto.keyword(),
			requstDto.toPageable());
		Page<CompetitionResponseDto> presentationCompetition = competitionMapper.toPresentationDtoPage(competition);
		return ResponseEntity.ok(new ApiResponse<>(CompetitionServiceCode.COMPETITION_SEARCH_SUCCESS.getCode(),
			presentationCompetition,
			CompetitionServiceCode.COMPETITION_SEARCH_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@GetMapping("/{competitionId}")
	@Operation(summary = "대회 단건 조회")
	public ResponseEntity<?> getCompetition(@PathVariable("competitionId") UUID competitionId) {
		CompetitionAppResponseDto competition = competitionService.getCompetition(competitionId);
		CompetitionResponseDto presentation = competitionMapper.toPresentationDto(competition);
		return ResponseEntity.ok(new ApiResponse<>(CompetitionServiceCode.COMPETITION_GET_SUCCESS.getCode(),
			presentation,
			CompetitionServiceCode.COMPETITION_GET_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@GetMapping("/{competitionId}/check")
	@Operation(summary = "주최 대회 확인")
	public ResponseEntity<?> checkCompetition(@PathVariable("competitionId") UUID userId,
		@ParameterObject GetRequestDto requestDto) {
		Page<CompetitionAppResponseDto> competition = competitionService.getHostCompetition(userId,
			requestDto.toPageable());
		Page<CompetitionResponseDto> presentationCompetition = competitionMapper.toPresentationDtoPage(competition);
		return ResponseEntity.ok(new ApiResponse<>(CompetitionServiceCode.COMPETITION_GET_SUCCESS.getCode(),
			presentationCompetition,
			CompetitionServiceCode.COMPETITION_GET_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@PatchMapping("/{competitionId}")
	@Operation(summary = "대회 수정")
	public ResponseEntity<?> updateCompetition(@PathVariable("competitionId") UUID competitionId,
		@RequestBody UpdateCompetitionRequestDto requestDto) {
		competitionService.updateCompetition(competitionId, requestDto.toCommandDto());
		return ResponseEntity.ok(new ApiResponse<>(CompetitionServiceCode.COMPETITION_UPDATE_SUCCESS.getCode(),
			"대회 수정이 완료되었습니다.",
			CompetitionServiceCode.COMPETITION_UPDATE_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));

	}

	@PatchMapping("/{competitionId}/delete")
	@Operation(summary = "대회 삭제")
	public ResponseEntity<?> deleteCompetition(@PathVariable("competitionId") UUID competitionId) {
		competitionService.deleteCompetition(competitionId);
		return ResponseEntity.ok(new ApiResponse<>(CompetitionServiceCode.COMPETITION_DELETE_SUCCESS.getCode(),
			"대회 삭제가 왼료되었습니다.",
			CompetitionServiceCode.COMPETITION_DELETE_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@PostMapping("/{competitionId}/apply")
	@Operation(summary = "대회 신청")
	public ResponseEntity<?> applyCompetition(@PathVariable("competitionId") UUID competitionId,
		@RequestBody ApplyCompetitionRequestDto participant) {
		competitionService.applyCompetition(competitionId, participant.getParticipantId());
		return ResponseEntity.ok(new ApiResponse<>(CompetitionServiceCode.COMPETITION_APPLY_SUCCESS.getCode(),
			"대회 신청에 성공했습니다.",
			CompetitionServiceCode.COMPETITION_APPLY_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	/*
	TODO : 결제 부분 미구현
	 */
	@PostMapping("/payment")
	@Operation(summary = "대회 결제")
	public ResponseEntity<?> payCompetition(@RequestBody Competition competition) {
		return ResponseEntity.ok(new ApiResponse<>(CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getCode(),
			"",
			CompetitionServiceCode.COMPETITION_CREATE_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	@PostMapping("/draw/{competitionId}")
	@Operation(summary = "대회 추첨")
	public ResponseEntity<?> drawCompetition(@PathVariable("competitionId") UUID competitionId) {
		competitionService.drawCompetition(competitionId);
		return ResponseEntity.ok(new ApiResponse<>(CompetitionServiceCode.COMPETITION_DRAW_SUCCESS.getCode(),
			"대회 추첨이 완료되었습니다.",
			CompetitionServiceCode.COMPETITION_DRAW_SUCCESS.getMessage(),
			HttpStatus.CREATED.value()));
	}

	/*
	TODO : 추첨 선정 인원 알림 전송
	 */

	//======================================================
	@PostMapping("/terms")
	public ResponseEntity<?> submitTerms(@RequestBody TermsAgreementRequestDto dto) {
		CompetitionApplicationEvent event = new CompetitionApplicationEvent(
			dto.getCompetitionId(),
			dto.getParticipantId(),
			ApplicationStep.TERMS,
			dto.isTermsAgreed(),
			null,
			null
		);
		applicationProducer.sendApplicationEvent(event);
		return ResponseEntity.ok("약관 동의 이벤트 발행 완료");
	}

	@PostMapping("/souvenir")
	public ResponseEntity<?> submitSouvenir(@RequestBody SouvenirSelectionRequestDto dto) {
		CompetitionApplicationEvent event = new CompetitionApplicationEvent(
			dto.getCompetitionId(),
			dto.getParticipantId(),
			ApplicationStep.SOUVENIR,
			null,
			dto.getSouvenirSelection(),
			null
		);
		applicationProducer.sendApplicationEvent(event);
		return ResponseEntity.ok("기념품 선택 이벤트 발행 완료");
	}

	@PostMapping("/shipping")
	public ResponseEntity<?> submitShipping(@RequestBody ShippingAddressRequestDto dto) {
		CompetitionApplicationEvent event = new CompetitionApplicationEvent(
			dto.getCompetitionId(),
			dto.getParticipantId(),
			ApplicationStep.SHIPPING,
			null,
			null,
			dto.getShippingAddress()
		);
		applicationProducer.sendApplicationEvent(event);
		return ResponseEntity.ok("배송지 입력 이벤트 발행 완료");
	}

	// ================================================= saga orchestrator ========================
	@PostMapping("/process")
	@Operation(summary = "대회 신청 단계 처리")
	public ResponseEntity<?> processApplicationStep(@RequestBody AppStepDto requestDto) {

			sagaService.processApplicationStep(
				requestDto.getStep(),
				requestDto.getCompetitionId(),
				requestDto.getParticipantId(),
				requestDto.getTermsAgreed(),
				requestDto.getSouvenirSelection(),
				requestDto.getShippingAddress()
			);

			return ResponseEntity.ok(new ApiResponse<>(
				CompetitionServiceCode.COMPETITION_APPLY_SUCCESS.getCode(),
				"대회 신청 단계 처리가 완료되었습니다.",
				CompetitionServiceCode.COMPETITION_APPLY_SUCCESS.getMessage(),
				HttpStatus.OK.value()
			));
	}

	@PostMapping("/complete")
	@Operation(summary = "대회 신청 전체 프로세스 처리")
	public ResponseEntity<?> completeApplication(@RequestBody CompleteAppDto requestDto) {

		String response = sagaService.processCompleteApplication(
			requestDto.getCompetitionId(),
			requestDto.getParticipantId(),
			requestDto.getTermsAgreed(),
			requestDto.getSouvenirSelection(),
			requestDto.getShippingAddress(),
			requestDto.getPaymentMethod(),
			requestDto.getPaymentStatus(),
			requestDto.getTransactionId());

			return ResponseEntity.ok(new ApiResponse<>(
				CompetitionServiceCode.COMPETITION_APPLY_SUCCESS.getCode(),
				response,
				CompetitionServiceCode.COMPETITION_APPLY_SUCCESS.getMessage(),
				HttpStatus.OK.value()
			));
	}

	@GetMapping("/{competitionId}/{participantId}/status")
	@Operation(summary = "대회 신청 상태 조회")
	public ResponseEntity<?> getApplicationStatus(
		@PathVariable("competitionId") UUID competitionId,
		@PathVariable("participantId") UUID participantId) {
		String result = sagaService.findActiveSagaId(competitionId, participantId);
		return ResponseEntity.ok(new ApiResponse<>(
			CompetitionServiceCode.COMPETITION_GET_SUCCESS.getCode(),
			result,
			CompetitionServiceCode.COMPETITION_GET_SUCCESS.getMessage(),
			HttpStatus.OK.value()
		));
	}

	@PostMapping("/apply/start")
	@Operation(summary = "대회 신청 Saga 시작")
	public ResponseEntity<?> startApplicationSaga(@RequestBody ApplyCompetitionRequestDto requestDto) {
		String sagaId = sagaOrchestrator.startApplicationSaga(
			requestDto.getCompetitionId(),
			requestDto.getParticipantId()
		);

		return ResponseEntity.ok(new ApiResponse<>(
			CompetitionServiceCode.COMPETITION_APPLY_SUCCESS.getCode(),
			sagaId,
			"대회 신청 Saga가 시작되었습니다.",
			HttpStatus.OK.value()
		));
	}

	@PostMapping("/apply/{sagaId}/terms")
	@Operation(summary = "약관 동의 단계 처리")
	public ResponseEntity<?> processTermsAgreement(
		@PathVariable("sagaId") String sagaId,
		@RequestBody TermsAgreementRequestDto requestDto) {

		sagaOrchestrator.processTermsAgreement(
			sagaId,
			requestDto.getCompetitionId(),
			requestDto.getParticipantId(),
			requestDto.isTermsAgreed()
		);

		return ResponseEntity.ok(new ApiResponse<>(
			CompetitionServiceCode.COMPETITION_APPLY_SUCCESS.getCode(),
			"약관 동의 처리가 완료되었습니다.",
			CompetitionServiceCode.COMPETITION_APPLY_SUCCESS.getMessage(),
			HttpStatus.OK.value()
		));
	}

	@PostMapping("/apply/{sagaId}/souvenir")
	@Operation(summary = "기념품 선택 단계 처리")
	public ResponseEntity<?> processSouvenirSelection(
		@PathVariable("sagaId") String sagaId,
		@RequestBody SouvenirSelectionRequestDto requestDto) {

		sagaOrchestrator.processSouvenirSelection(
			sagaId,
			requestDto.getCompetitionId(),
			requestDto.getParticipantId(),
			requestDto.getSouvenirSelection()
		);

		return ResponseEntity.ok(new ApiResponse<>(
			CompetitionServiceCode.COMPETITION_APPLY_SUCCESS.getCode(),
			"기념품 선택 처리가 완료되었습니다.",
			CompetitionServiceCode.COMPETITION_APPLY_SUCCESS.getMessage(),
			HttpStatus.OK.value()
		));
	}

	@PostMapping("/apply/{sagaId}/shipping")
	@Operation(summary = "배송지 입력 단계 처리")
	public ResponseEntity<?> processShippingAddress(
		@PathVariable("sagaId") String sagaId,
		@RequestBody ShippingAddressRequestDto requestDto) {

		sagaOrchestrator.processShippingAddress(
			sagaId,
			requestDto.getCompetitionId(),
			requestDto.getParticipantId(),
			requestDto.getShippingAddress()
		);

		return ResponseEntity.ok(new ApiResponse<>(
			CompetitionServiceCode.COMPETITION_APPLY_SUCCESS.getCode(),
			"배송지 입력 처리가 완료되었습니다. 결제 단계로 진행됩니다.",
			CompetitionServiceCode.COMPETITION_APPLY_SUCCESS.getMessage(),
			HttpStatus.OK.value()
		));
	}

	@PostMapping("/apply/{sagaId}/payment/init")
	@Operation(summary = "결제 시작 단계")
	public ResponseEntity<?> initiatePayment(
		@PathVariable("sagaId") String sagaId,
		@RequestBody PaymentRequestDto requestDto) {

		sagaOrchestrator.initiatePayment(
			sagaId,
			requestDto.getCompetitionId(),
			requestDto.getParticipantId(),
			requestDto.getPaymentMethod()
		);

		return ResponseEntity.ok(new ApiResponse<>(
			CompetitionServiceCode.COMPETITION_APPLY_SUCCESS.getCode(),
			"결제가 시작되었습니다.",
			CompetitionServiceCode.COMPETITION_APPLY_SUCCESS.getMessage(),
			HttpStatus.OK.value()
		));
	}

	// 결제 완료 단계 추가
	@PostMapping("/apply/{sagaId}/payment/complete")
	@Operation(summary = "결제 완료 단계")
	public ResponseEntity<?> completePayment(
		@PathVariable("sagaId") String sagaId,
		@RequestBody PaymentRequestDto requestDto) {

		// 결제 완료 처리
		sagaOrchestrator.completePayment(
			sagaId,
			requestDto.getCompetitionId(),
			requestDto.getParticipantId(),
			requestDto.getAmount(),
			requestDto.getPaymentMethod(),
			requestDto.getPaymentStatus(),
			requestDto.getTransactionId()
		);

		return ResponseEntity.ok(new ApiResponse<>(
			CompetitionServiceCode.COMPETITION_APPLY_SUCCESS.getCode(),
			"결제가 완료되었습니다. 신청 자격 확인 중입니다.",
			CompetitionServiceCode.COMPETITION_APPLY_SUCCESS.getMessage(),
			HttpStatus.OK.value()
		));
	}

	@PostMapping("/cancel")
	@Operation(summary = "대회 취소 Saga 시작")
	public ResponseEntity<?> startCancellationSaga(@RequestBody CancelParticipantRequestDto requestDto) {
		String sagaId = sagaOrchestrator.startCancellationSaga(
			requestDto.getCompetitionId(),
			requestDto.getParticipantId(),
			requestDto.getReason(),
			requestDto.isRefundRequired()
		);

		return ResponseEntity.ok(new ApiResponse<>(
			CompetitionServiceCode.PARTICIPANT_CANCEL_SUCCESS.getCode(),
			sagaId,
			"대회 취소 Saga가 시작되었습니다.",
			HttpStatus.OK.value()
		));
	}

}
