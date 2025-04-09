package com._42195km.msa.competitionservice.infrastructure.persistence;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com._42195km.msa.competitionservice.domain.model.Competition;

public interface CompetitionJpaRepository extends JpaRepository <Competition, UUID>{

	Page<Competition> findByIsDeletedFalse(Pageable pageable);
}
