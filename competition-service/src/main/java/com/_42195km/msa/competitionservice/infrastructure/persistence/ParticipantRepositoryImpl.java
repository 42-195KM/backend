package com._42195km.msa.competitionservice.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
	private final CompetitionParticipantMappingJpaRepository mappingJpaRepository;

	public Participant save(Participant participant) {
		return jpaRepository.save(participant);
	}

	public Page<Object> searchByTitle(String keyword, Pageable pageable){
		return mappingJpaRepository.searchByTitle(keyword, pageable);
	}

	public Page<Object> searchByCompetitionType(String keyword, Pageable pageable){
		return mappingJpaRepository.searchByCompetitionType(keyword, pageable);
	}
	;
	public Page<Object> searchByReceptionType(String keyword, Pageable pageable){
		return mappingJpaRepository.searchByReceptionType(keyword, pageable);
	}
	public Page<Object> searchByStatue(String keyword, Pageable pageable){
		return mappingJpaRepository.searchByStatue(keyword, pageable);
	}
	public Page<Object> searchByUuid(UUID uuid, Pageable pageable){
		return mappingJpaRepository.searchByUuid(uuid, pageable);
	}
}
