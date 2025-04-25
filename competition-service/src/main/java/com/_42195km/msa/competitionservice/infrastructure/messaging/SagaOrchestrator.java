package com._42195km.msa.competitionservice.infrastructure.messaging;

import java.util.UUID;

public interface SagaOrchestrator {

	String startApplicationSaga(UUID competitionId, UUID participantId);

	void processTermsAgreement(String sagaId, UUID competitionId, UUID participantId, Boolean termsAgreed);

	void processSouvenirSelection(String sagaId, UUID competitionId, UUID participantId, String souvenirSelection);

	void processShippingAddress(String sagaId, UUID competitionId, UUID participantId, String shippingAddress);

	void initiatePayment(String sagaId, UUID competitionId, UUID participantId, String paymentMethod);

	void completePayment(String sagaId, UUID competitionId, UUID participantId, Integer amount,
		String paymentMethod, String paymentStatus, String transactionId);
}
