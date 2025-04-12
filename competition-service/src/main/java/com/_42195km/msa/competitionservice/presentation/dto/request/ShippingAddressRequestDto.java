package com._42195km.msa.competitionservice.presentation.dto.request;

import java.util.UUID;

import lombok.Getter;

@Getter
public class ShippingAddressRequestDto {
	private UUID competitionId;
	private UUID participantId;
	private String shippingAddress;
}
