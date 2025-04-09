package com._42195km.msa.competitionservice.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com._42195km.msa.common.exception.CustomBusinessException;
import com._42195km.msa.competitionservice.application.exception.CompetitionServiceCode;
import com._42195km.msa.competitionservice.domain.model.Participant;
import com._42195km.msa.competitionservice.domain.repository.ParticipantRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ParticipantRepositoryImpl implements ParticipantRepository {
	private final ParticipantJpaRepository jpaRepository;

	public Participant findById(UUID userid) {
		return jpaRepository.findByIdAndIsDeletedFalse(userid);
	}

	public Participant save(Participant participant) {
		return jpaRepository.save(participant);
	}

}
