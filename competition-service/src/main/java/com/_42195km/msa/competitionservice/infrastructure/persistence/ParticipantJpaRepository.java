package com._42195km.msa.competitionservice.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com._42195km.msa.competitionservice.domain.model.Participant;

public interface ParticipantJpaRepository extends JpaRepository<Participant, UUID> {

	Page<Participant> findByParticipantId(UUID participantId, Pageable pageable);

}
