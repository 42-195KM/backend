package com._42195km.msa.competitionservice.presentation.dto.request;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDto {
	@Schema(example = "9191cce5-4e4a-49ed-bf6e-56616e2c8be4")
	private UUID competitionId;

	@Schema(example = "1845b196-7132-47c8-a233-193a6ebf5278")
	private UUID participantId;

	@Schema(example = "100000")
	private Integer amount;

	@Schema(example = "CARD")
	private String paymentMethod;

	@Schema(example = "SUCCESS")
	private String paymentStatus;

	@Schema(example = "pay_1234567890")
	private String transactionId;
}
