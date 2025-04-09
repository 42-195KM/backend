package com._42195km.msa.competitionservice.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import com._42195km.msa.competitionservice.domain.repository.ParticipantRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ParticipantRepositoryImpl implements ParticipantRepository {
	private final ParticipantJpaRepository jpaRepository;

}
