package com._42195km.msa.competitionservice.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com._42195km.msa.competitionservice.domain.model.Competition;

public interface CompetitionRepository {

	Competition save(Competition competition);

	Page<Competition> findAll(Pageable pageable);
}
