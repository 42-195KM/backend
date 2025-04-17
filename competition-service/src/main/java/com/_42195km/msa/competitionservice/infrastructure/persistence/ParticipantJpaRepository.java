package com._42195km.msa.competitionservice.infrastructure.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com._42195km.msa.competitionservice.domain.model.Participant;

public interface ParticipantJpaRepository extends JpaRepository<Participant, UUID> {

	Participant findByParticipantId(UUID participantId);

}
