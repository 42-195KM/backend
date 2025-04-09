package com._42195km.msa.competitionservice.infrastructure.persistence;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com._42195km.msa.competitionservice.domain.model.Competition;
import com._42195km.msa.competitionservice.domain.model.CompetitionParticipantMapping;
import com._42195km.msa.competitionservice.domain.model.Participant;
import com._42195km.msa.competitionservice.domain.repository.CompetitionParticipantMapptingRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CompetitionParticipantMappingRepositoryImpl implements CompetitionParticipantMapptingRepository {
	private final CompetitionParticipantMappingJpaRepository  jpaRepository;

	public Boolean checkIsParticipate(UUID participantId, UUID competitionId){
		return jpaRepository.existsByParticipantIdAndCompetitionId(participantId, competitionId);
	}

	public Integer checkParticipantCount(Competition competition,Participant participant){
		return jpaRepository.countByCompetitionAndParticipant(competition, participant);
	}

	public CompetitionParticipantMapping save(CompetitionParticipantMapping competitionParticipantMapping){
		return jpaRepository.save(competitionParticipantMapping);

	}
}
