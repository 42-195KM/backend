package com._42195km.msa.competitionservice.infrastructure.messaging;

import java.util.UUID;

public interface SagaEventPublisher {
	void publishTermsAgreementEvent(UUID competitionId, UUID participantId, Boolean termsAgreed);
	void publishSouvenirSelectionEvent(UUID competitionId, UUID participantId, String souvenirSelection);
	void publishShippingAddressEvent(UUID competitionId, UUID participantId, String shippingAddress);
	void publishPaymentCompletedEvent(UUID competitionId, UUID participantId,
		Integer amount, String paymentMethod,
		String paymentStatus, String transactionId);
}