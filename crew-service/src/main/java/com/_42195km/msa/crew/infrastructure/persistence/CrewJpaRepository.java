package com._42195km.msa.crew.infrastructure.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com._42195km.msa.crew.domain.model.Crew;

public interface CrewJpaRepository extends JpaRepository<Crew, UUID> {
}
