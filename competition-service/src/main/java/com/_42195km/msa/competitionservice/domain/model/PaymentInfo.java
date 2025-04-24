package com._42195km.msa.competitionservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentInfo {
	private String paymentMethod;
	private String paymentStatus;
	private String transactionId;
}

