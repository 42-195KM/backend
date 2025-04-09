package com._42195km.msa.competitionservice.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com._42195km.msa.competitionservice.domain.model.Competition;
import com._42195km.msa.competitionservice.domain.repository.CompetitionRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CompetitionRepositoryImpl implements CompetitionRepository {

	private final CompetitionJpaRepository jpaRepository;

	@Override
	public Competition save(Competition competition) {
		return jpaRepository.save(competition);
	}

	@Override
	public Page<Competition> findAll(Pageable pageable) {
		return jpaRepository.findByIsDeletedFalse(pageable);
	}

}
