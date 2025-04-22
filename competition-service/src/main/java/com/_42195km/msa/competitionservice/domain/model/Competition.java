package com._42195km.msa.competitionservice.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import com._42195km.msa.common.BaseEntity;
import com._42195km.msa.common.exception.CustomBusinessException;
import com._42195km.msa.competitionservice.application.dto.request.CreateCompetitionCommandDto;
import com._42195km.msa.competitionservice.application.dto.request.UpdateCompetitionCommandDto;
import com._42195km.msa.competitionservice.application.exception.CompetitionServiceCode;
import com._42195km.msa.competitionservice.infrastructure.messaging.SagaEventPublisher;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "p_competition")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Competition extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "user_id")
	private UUID userId;

	private String title;

	@Enumerated(EnumType.STRING)
	private CompetitionType type;

	@Column(name = "reception_type")
	@Enumerated(EnumType.STRING)
	private ReceptionType receptionType;

	@Column(name = "participants_num")
	private Integer participantsNum;

	private Integer price;

	@OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CompetitionParticipantMapping> participantMappings = new ArrayList<>();

	@Builder
	public Competition(UUID userId, String title, CompetitionType type, ReceptionType receptionType,
		Integer participantsNum, Integer price) {
		this.userId = userId;
		this.title = title;
		this.type = type;
		this.receptionType = receptionType;
		this.participantsNum = participantsNum;
		this.price = price;
	}

	public static Competition create(CreateCompetitionCommandDto commandDto) {
		return Competition.builder()
			.userId(commandDto.getUserId())
			.title(commandDto.getTitle())
			.type(commandDto.getType())
			.receptionType(commandDto.getReceptionType())
			.participantsNum(commandDto.getParticipantsNum())
			.price(commandDto.getPrice())
			.build();
	}

	public void update(UpdateCompetitionCommandDto commandDto) {
		if (commandDto.getTitle() != null) {
			this.title = commandDto.getTitle();
		}
		if (commandDto.getType() != null) {
			this.type = commandDto.getType();
		}
		if (commandDto.getReceptionType() != null) {
			this.receptionType = commandDto.getReceptionType();
		}
		if (commandDto.getParticipantsNum() != null) {
			this.participantsNum = commandDto.getParticipantsNum();
		}
		if (commandDto.getPrice() != null) {
			this.price = commandDto.getPrice();
		}
	}

	// 참가 신청 확인 (중복 신청 체크)
	public boolean isParticipantAlreadyRegistered(UUID participantId) {
		return participantMappings.stream()
			.anyMatch(mapping ->
				mapping.getParticipant().getParticipantId().equals(participantId) &&
					mapping.getStatus() != Status.CANCEL);
	}

	// 정원 확인
	public boolean isCompetitionFull() {
		if (this.receptionType != ReceptionType.FIRST) {
			return false;
		}

		long activeParticipants = participantMappings.stream()
			.filter(mapping -> mapping.getStatus() != Status.CANCEL)
			.count();

		return activeParticipants >= this.participantsNum;
	}

	// 참가자 신청 생성
	public CompetitionParticipantMapping createParticipantApplication(Participant participant) {
		CompetitionParticipantMapping mapping = CompetitionParticipantMapping.create(this, participant);
		participantMappings.add(mapping);
		return mapping;
	}

	// 참가자 매핑 조회
	public Optional<CompetitionParticipantMapping> findParticipantMapping(UUID participantId) {
		return participantMappings.stream()
			.filter(mapping -> mapping.getParticipant().getParticipantId().equals(participantId))
			.findFirst();
	}

	// 추첨 실행
	public void performDraw() {
		if (this.receptionType != ReceptionType.DRAW) {
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_DRAW_INVALID_TYPE);
		}

		List<CompetitionParticipantMapping> applicants = participantMappings.stream()
			.filter(mapping -> mapping.getStatus() == Status.APPLY)
			.collect(Collectors.toList());

		if (applicants.size() <= this.participantsNum) {
			// 신청자가 정원보다 적거나 같으면 전원 선정
			applicants.forEach(CompetitionParticipantMapping::markAsSelected);
		} else {
			// 랜덤 추첨 진행
			Collections.shuffle(applicants);
			applicants.stream()
				.limit(this.participantsNum)
				.forEach(CompetitionParticipantMapping::markAsSelected);
		}
	}

	@Transactional
	public CompetitionParticipantMapping processApplicationStep(UUID participantId,
		Participant participant,
		ApplicationStep targetStep,
		Object stepData,
		SagaEventPublisher eventPublisher) {
		// 1. 참가자 매핑 조회 또는 생성
		CompetitionParticipantMapping mapping = findParticipantMapping(participantId)
			.orElseGet(() -> {
				// 중복 신청 확인
				if (isParticipantAlreadyRegistered(participantId)) {
					throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_APPLY_EXIST);
				}
				return createParticipantApplication(participant);
			});

		// 2. 현재 단계 확인
		ApplicationStep currentStep = mapping.getApplicationStep();

		// 3. 목표 단계와 현재 단계가 맞는지 확인
		if (currentStep != targetStep) {
			throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_APPLY_FAIL);
		}

		// 4. 단계별 처리
		switch (targetStep) {
			case TERMS_AGREEMENT:
				Boolean termsAgreed = (Boolean) stepData;
				mapping.updateTermsAgreement(termsAgreed);
				// 이벤트 발행
				if (eventPublisher != null) {
					eventPublisher.publishTermsAgreementEvent(this.id, participantId, termsAgreed);
				}
				break;

			case SOUVENIR_SELECTION:
				String souvenirSelection = (String) stepData;
				mapping.updateSouvenirSelection(souvenirSelection);
				// 이벤트 발행
				if (eventPublisher != null) {
					eventPublisher.publishSouvenirSelectionEvent(this.id, participantId, souvenirSelection);
				}
				break;

			case SHIPPING_ADDRESS:
				String shippingAddress = (String) stepData;
				mapping.updateShippingAddress(shippingAddress);
				// 이벤트 발행
				if (eventPublisher != null) {
					eventPublisher.publishShippingAddressEvent(this.id, participantId, shippingAddress);
				}
				break;

			case PAYMENT_PENDING:
				// 결제 단계에서 정원 확인
				if (isCompetitionFull()) {
					throw CustomBusinessException.from(CompetitionServiceCode.COMPETITION_APPLY_FIRST_FAIL);
				}

				PaymentInfo paymentInfo = (PaymentInfo) stepData;
				mapping.updatePaymentInfo(
					paymentInfo.getPaymentMethod(),
					paymentInfo.getPaymentStatus(),
					paymentInfo.getTransactionId()
				);

				// 결제 성공 시 참가 확정
				if ("SUCCESS".equals(paymentInfo.getPaymentStatus())) {
					mapping.confirmParticipation();
					// 이벤트 발행
					if (eventPublisher != null) {
						eventPublisher.publishPaymentCompletedEvent(
							this.id, participantId, this.price,
							paymentInfo.getPaymentMethod(),
							paymentInfo.getPaymentStatus(),
							paymentInfo.getTransactionId()
						);
					}
				}
				break;

			default:
				throw new IllegalArgumentException("지원하지 않는 단계입니다: " + targetStep);
		}

		return mapping;
	}
}
