package com._42195km.msa.competitionservice.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "P_competition_participant")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Participant {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "participant_id")
	private UUID participantId;

	@OneToMany(mappedBy = "participant", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CompetitionParticipantMapping> competitionMappings = new ArrayList<>();

}
