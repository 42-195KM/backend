package com._42195km.msa.crew.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com._42195km.msa.crew.domain.model.Crew;

public interface CrewRepository {
	Crew save(Crew crew);

	boolean existsByName(String name);

	Optional<Crew> findById(UUID crewId);

	Page<Crew> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String nameKeyword,
		String descriptionKeyword, Pageable pageable);
}
