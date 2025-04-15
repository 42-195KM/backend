package com._42195km.msa.crew.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com._42195km.msa.crew.domain.model.Crew;
import com._42195km.msa.crew.domain.model.CrewMemberMapping;

public interface CrewJpaRepository extends JpaRepository<Crew, UUID> {
	boolean existsByName(String name);

	Optional<Crew> findByIdAndDeletedAtIsNull(UUID crewId);

	Page<Crew> findAllByDeletedAtIsNull(Pageable pageable);

	Page<Crew> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(String nameKeyword,
		String descriptionKeyword, Pageable pageable);

	@Query("SELECT cmm from CrewMemberMapping cmm JOIN cmm.crewMember cm WHERE cmm.crew.id = :crewId AND cmm.deletedAt IS NULL")
	Page<CrewMemberMapping> findAllCrewMemberMappingByCrewId(@Param(value = "crewId") UUID crewId, Pageable pageable);
}
