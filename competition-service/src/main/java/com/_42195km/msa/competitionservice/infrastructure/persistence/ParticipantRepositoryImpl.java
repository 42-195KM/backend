package com._42195km.msa.competitionservice.infrastructure.persistence;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com._42195km.msa.competitionservice.domain.model.Participant;
import com._42195km.msa.competitionservice.domain.repository.ParticipantRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ParticipantRepositoryImpl implements ParticipantRepository {
	private final ParticipantJpaRepository jpaRepository;
	private final CompetitionParticipantMappingJpaRepository mappingJpaRepository;

	public Participant save(Participant participant) {
		return jpaRepository.save(participant);
	}

	public Page<Object> searchByTitle(String keyword, Pageable pageable) {
		return mappingJpaRepository.searchByTitle(keyword, pageable);
	}

	public Page<Object> searchByCompetitionType(String keyword, Pageable pageable) {
		return mappingJpaRepository.searchByCompetitionType(keyword, pageable);
	}

	;

	public Page<Object> searchByReceptionType(String keyword, Pageable pageable) {
		return mappingJpaRepository.searchByReceptionType(keyword, pageable);
	}

	public Page<Object> searchByStatus(String keyword, Pageable pageable) {
		return mappingJpaRepository.searchByStatus(keyword, pageable);
	}

	public Page<Object> searchByUuid(String uuid, Pageable pageable) {
		return mappingJpaRepository.searchByUuid(uuid, pageable);
	}

	public Page<Object> getByUuid(UUID uuid, Pageable pageable) {
		return mappingJpaRepository.getByUuid(uuid, pageable);
	}

	public Participant findByParticipantId(UUID participantId) {
		return jpaRepository.findByParticipantId(participantId);
	}
}
