package com._42195km.msa.competitionservice.application.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class AppStepDto {
	private UUID competitionId;
	private UUID participantId;
	private String step; // "TERMS", "SOUVENIR", "SHIPPING"

	private Boolean termsAgreed;

	private String souvenirSelection;

	private String shippingAddress;
}
