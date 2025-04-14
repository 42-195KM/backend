package com._42195km.msa.competitionservice.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com._42195km.msa.competitionservice.domain.model.ParticipantDetail;
import com._42195km.msa.competitionservice.domain.repository.ParticipantDetailRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ParticipantDetailRepositoryImpl implements ParticipantDetailRepository {
	private final ParticipantDetailJpaRepository jpaRepository;

	public Optional<ParticipantDetail> findByCompetitionIdAndParticipantId(UUID competitionId, UUID participantId){
		return jpaRepository.findByCompetitionIdAndParticipantId(competitionId, participantId);
	}

	public void save(ParticipantDetail participantDetail){
		jpaRepository.save(participantDetail);
	}
}
