package com._42195km.msa.competitionservice.infrastructure.persistence;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com._42195km.msa.common.exception.CustomBusinessException;
import com._42195km.msa.competitionservice.application.exception.CompetitionServiceCode;
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

	@Override
	public Page<Competition> searchByTitle(String keyword, Pageable pageable) {
		return jpaRepository.searchByTitle(keyword, pageable);
	}

	@Override
	public Page<Competition> searchByEnumType(String keyword, Pageable pageable) {
		return jpaRepository.searchByEnumType(keyword, pageable);
	}

	@Override
	public Competition findById(UUID id) {
		return jpaRepository.findById(id).orElseThrow(()-> CustomBusinessException.from(CompetitionServiceCode.COMPETITION_GET_ID_FAIL));
	}

	public Page<Competition> findByHost(UUID hostId,Pageable pageable){
		return jpaRepository.findByUserIdAndIsDeletedFalse(hostId, pageable);
	}

}
