package com._42195km.msa.competitionservice.domain.model;

import java.io.Serializable;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompetitionApplicationData implements Serializable {
	private UUID competitionId;
	private UUID participantId;
	private Boolean termsAgreed;
	private String souvenirSelection;
	private String shippingAddress;

	public CompetitionApplicationData(UUID competitionId, UUID participantId) {
		this.competitionId = competitionId;
		this.participantId = participantId;
	}

	public void checkTerm(Boolean termsAgreed){
		this.termsAgreed = termsAgreed;
	}

	public void checkSouvenirSelection(String souvenirSelection){
		this.souvenirSelection = souvenirSelection;
	}

	public void checkShippingAddress(String shippingAddress){
		this.shippingAddress = shippingAddress;
	}
}
