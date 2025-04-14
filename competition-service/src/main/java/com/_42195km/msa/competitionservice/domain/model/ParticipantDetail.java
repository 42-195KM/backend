package com._42195km.msa.competitionservice.domain.model;

import java.util.UUID;

import com._42195km.msa.common.BaseEntity;
import com._42195km.msa.competitionservice.application.dto.request.UpdateCompetitionCommandDto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "p_participant_detail", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"competition_id", "participant_id"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParticipantDetail extends BaseEntity {
	@Id
	@GeneratedValue
	private UUID id;

	private UUID competitionId;
	private UUID participantId;

	private Boolean termsAgreed;
	private String souvenirSelection;
	private String shippingAddress;

	@Builder
	public ParticipantDetail(UUID competitionId, UUID participantId, Boolean termsAgreed, String souvenirSelection, String shippingAddress) {
		this.competitionId = competitionId;
		this.participantId = participantId;
		this.termsAgreed = termsAgreed;
		this.souvenirSelection = souvenirSelection;
		this.shippingAddress = shippingAddress;
	}

	public void update(SagaState sagaState) {
		if (sagaState.getTermsAgreed() != null) {
			this.termsAgreed = sagaState.getTermsAgreed();
		}
		if (sagaState.getSouvenirSelection() != null) {
			this.souvenirSelection = sagaState.getSouvenirSelection();
		}
		if (sagaState.getShippingAddress() != null) {
			this.shippingAddress = sagaState.getShippingAddress();
		}
	}

}
