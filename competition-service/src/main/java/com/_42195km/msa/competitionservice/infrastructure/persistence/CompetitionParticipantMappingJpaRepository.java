package com._42195km.msa.competitionservice.infrastructure.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com._42195km.msa.competitionservice.domain.model.Competition;
import com._42195km.msa.competitionservice.domain.model.CompetitionParticipantMapping;
import com._42195km.msa.competitionservice.domain.model.Participant;

public interface CompetitionParticipantMappingJpaRepository extends JpaRepository<CompetitionParticipantMapping, UUID> {

	Boolean existsByParticipantIdAndCompetitionId(UUID participantId, UUID competitionId);

	Integer countByCompetitionAndParticipant(Competition competition, Participant participant);
}
