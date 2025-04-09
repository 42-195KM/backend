package com._42195km.msa.competitionservice.domain.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com._42195km.msa.competitionservice.domain.model.Competition;

public interface CompetitionRepository {

	Competition save(Competition competition);

	Page<Competition> findAll(Pageable pageable);

	Page<Competition> searchByTitle(@Param("keyword") String keyword, Pageable pageable);

	Page<Competition> searchByEnumType(@Param("keyword") String keyword, Pageable pageable);

	Competition findById(UUID id);
}
