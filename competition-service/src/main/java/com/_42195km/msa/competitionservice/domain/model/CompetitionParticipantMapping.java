package com._42195km.msa.competitionservice.domain.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Table(name = "p_competition_participant_mapping")
@Entity
public class CompetitionParticipantMapping {

	@Id
	private UUID id;

	@Column(name = "participant_id")
	private UUID participantId;

	@Column(name = "competition_id")
	private UUID competitionId;

	@ManyToOne
	@JoinColumn(name = "competition_id", nullable = false)
	private Competition competition;

	@ManyToOne
	@JoinColumn(name = "participant_id", nullable = false)
	private Participant participant;
}
