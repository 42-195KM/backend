package com._42195km.msa.competitionservice.application.event;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CompetitionApplicationEvent {
	private UUID competitionId;
	private UUID participantId;
	private ApplicationStep step;
	private Boolean termsAgreed;
	private String souvenirSelection;
	private String shippingAddress;
}
