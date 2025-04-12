package com._42195km.msa.competitionservice.infrastructure.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com._42195km.msa.competitionservice.application.event.CompetitionApplicationEvent;

@Component
public class CompetitionApplicationStateConsumer {
	private final CompetitionApplicationStateManager stateManager;

	public CompetitionApplicationStateConsumer(CompetitionApplicationStateManager stateManager) {
		this.stateManager = stateManager;
	}

	@KafkaListener(topics = "competition_application", groupId = "competition_state_group")
	public void listenApplicationEvent(CompetitionApplicationEvent event) {
		stateManager.processEvent(event);
	}
}
