package com._42195km.msa.competitionservice.domain.model;

import java.util.UUID;

import com._42195km.msa.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table(name = "p_competition_participant_mapping")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompetitionParticipantMapping extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne
	@JoinColumn(name = "competition_id", nullable = false)
	private Competition competition;

	@ManyToOne
	@JoinColumn(name = "participant _id", referencedColumnName = "participant_user_id", nullable = false)
	private Participant participant;

	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	private Status status = Status.APPLY;


	@Builder
	public CompetitionParticipantMapping(Competition competition, Participant participant) {
		this.competition = competition;
		this.participant = participant;
	}

	public static CompetitionParticipantMapping create(Competition competition, Participant participant) {
		return CompetitionParticipantMapping.builder().competition(competition).participant(participant).build();
	}

	public void markAsSelected() {
		this.status = Status.SELECTED;
	}

	public void cancel() {
		this.status = Status.CANCEL;
	}

}
