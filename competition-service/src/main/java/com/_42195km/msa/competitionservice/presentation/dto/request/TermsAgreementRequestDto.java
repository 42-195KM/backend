package com._42195km.msa.competitionservice.presentation.dto.request;

import java.util.UUID;

import lombok.Getter;

@Getter
public class TermsAgreementRequestDto {
	private UUID competitionId;
	private UUID participantId;
	private boolean termsAgreed;
}
