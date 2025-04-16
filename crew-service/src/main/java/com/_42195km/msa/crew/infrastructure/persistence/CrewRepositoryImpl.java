package com._42195km.msa.crew.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com._42195km.msa.crew.domain.model.Crew;
import com._42195km.msa.crew.domain.model.CrewMemberMapping;
import com._42195km.msa.crew.domain.repository.CrewRepository;

@Repository
public class CrewRepositoryImpl implements CrewRepository {
	private final CrewJpaRepository crewJpaRepository;

	public CrewRepositoryImpl(CrewJpaRepository crewJpaRepository) {
		this.crewJpaRepository = crewJpaRepository;
	}

	@Override
	public Crew save(Crew crew) {
		return crewJpaRepository.save(crew);
	}

	@Override
	public boolean existsByName(String name) {
		return crewJpaRepository.existsByName(name);
	}

	@Override
	public Optional<Crew> findByIdAndDeletedAtIsNull(UUID crewId) {
		return crewJpaRepository.findByIdAndDeletedAtIsNull(crewId);
	}


	@Override
	public Page<Crew> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(
		String nameKeyword,
		String descriptionKeyword, Pageable pageable) {
		return crewJpaRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(
			nameKeyword,
			descriptionKeyword, pageable);
	}

	@Override
	public Page<Crew> findAllByDeletedAtIsNull(Pageable pageable) {
		return crewJpaRepository.findAllByDeletedAtIsNull(pageable);
	}

	@Override
	public Page<CrewMemberMapping> findAllCrewMemberMappingByCrewId(UUID crewId, Pageable pageable) {
		return crewJpaRepository.findAllCrewMemberMappingByCrewId(crewId, pageable);
	}
}
