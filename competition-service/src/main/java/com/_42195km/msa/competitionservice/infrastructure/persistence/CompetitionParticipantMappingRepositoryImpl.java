package com._42195km.msa.competitionservice.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import com._42195km.msa.competitionservice.domain.repository.CompetitionParticipantMapptingRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CompetitionParticipantMappingRepositoryImpl implements CompetitionParticipantMapptingRepository {
	private final CompetitionParticipantMappingJpaRepository  jpaRepository;
}
