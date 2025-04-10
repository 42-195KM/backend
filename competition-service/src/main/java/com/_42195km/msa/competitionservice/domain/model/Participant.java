package com._42195km.msa.competitionservice.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com._42195km.msa.common.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "p_competition_participant")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Participant extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "participant_id")
	private UUID participantId;


	@OneToMany(mappedBy = "participant", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CompetitionParticipantMapping> competitionMappings = new ArrayList<>();

	@Builder
	public Participant(UUID participantId) {
		this.participantId = participantId;
	}

	public static Participant create(UUID participantId) {
		return Participant.builder().participantId(participantId).build();
	}

}
