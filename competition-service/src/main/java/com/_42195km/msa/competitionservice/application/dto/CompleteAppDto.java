package com._42195km.msa.competitionservice.application.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CompleteAppDto {
	@Schema(example = "872dd912-8616-4595-aa94-5737bf633012")
	private UUID competitionId;

	@Schema(example = "1845b196-7132-47c8-a233-193a6ebf5278")
	private UUID participantId;

	@Schema(example = "true")
	private Boolean termsAgreed;

	@Schema(example = "optionA")
	private String souvenirSelection;

	@Schema(example = "서울시 강남구 테헤란로 123")
	private String shippingAddress;

	@Schema(example = "CARD", description = "결제 방식 (CARD, BANK_TRANSFER 등)")
	private String paymentMethod;

	@Schema(example = "SUCCESS", description = "결제 상태 (SUCCESS, FAILED 등)")
	private String paymentStatus;

	@Schema(example = "pay_1234567890", description = "결제 트랜잭션 ID")
	private String transactionId;

}
