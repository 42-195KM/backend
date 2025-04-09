package com._42195km.msa.competitionservice.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import com._42195km.msa.competitionservice.domain.repository.CompetitionRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CompetitionRepositoryImpl implements CompetitionRepository {
	private final CompetitionJpaRepository jpaRepository;
}
