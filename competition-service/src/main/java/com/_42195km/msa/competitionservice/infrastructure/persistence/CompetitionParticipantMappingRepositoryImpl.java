package com._42195km.msa.competitionservice.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com._42195km.msa.competitionservice.domain.model.Competition;
import com._42195km.msa.competitionservice.domain.model.CompetitionParticipantMapping;
import com._42195km.msa.competitionservice.domain.repository.CompetitionParticipantMapptingRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CompetitionParticipantMappingRepositoryImpl implements CompetitionParticipantMapptingRepository {
	private final CompetitionParticipantMappingJpaRepository jpaRepository;

	public Boolean checkIsParticipate(UUID participantId, UUID competitionId) {
		return jpaRepository.existsByParticipantIdAndCompetitionId(participantId, competitionId);
	}

	public long countByCompetition(Competition competition) {
		return jpaRepository.countByCompetition(competition);
	}

	public CompetitionParticipantMapping save(CompetitionParticipantMapping competitionParticipantMapping) {
		return jpaRepository.save(competitionParticipantMapping);
	}

	public List<CompetitionParticipantMapping> findAllByCompetition(Competition competition) {
		return jpaRepository.findAllByCompetition(competition);
	}

	public Page<CompetitionParticipantMapping> findParticipants(UUID competitionId, Pageable pageable) {
		return jpaRepository.findParticipantsByCompetitionId(competitionId, pageable);
	}

	public CompetitionParticipantMapping findByCompetitionIdAndParticipantId(UUID competitionId, UUID participantId) {
		return jpaRepository.findByCompetitionIdAndParticipantId(competitionId, participantId);
	}
}
