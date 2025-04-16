package com._42195km.msa.competitionservice.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com._42195km.msa.competitionservice.domain.model.ParticipantDetail;

public interface ParticipantDetailJpaRepository extends JpaRepository<ParticipantDetail, Long> {
	Optional<ParticipantDetail> findByCompetitionIdAndParticipantId(UUID competitionId, UUID participantId);
}
