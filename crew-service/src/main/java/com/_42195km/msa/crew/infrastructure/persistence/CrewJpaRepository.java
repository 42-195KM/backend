package com._42195km.msa.crew.infrastructure.persistence;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com._42195km.msa.crew.domain.model.Crew;

public interface CrewJpaRepository extends JpaRepository<Crew, UUID> {
	boolean existsByName(String name);

	Page<Crew> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String nameKeyword,
		String descriptionKeyword, Pageable pageable);
}
