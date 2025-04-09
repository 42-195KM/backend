package com._42195km.msa.competitionservice.domain.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Table(name = "p_competition_participant_mapping")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompetitionParticipantMapping {

	@Id
	private UUID id;

	@ManyToOne
	@JoinColumn(name = "competition_id", nullable = false)
	private Competition competition;

	@ManyToOne
	@JoinColumn(name = "participant_id", nullable = false)
	private Participant participant;
}
