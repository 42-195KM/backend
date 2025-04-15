package com._42195km.msa.crew.domain.repository;

import com._42195km.msa.crew.domain.model.Crew;

public interface CrewRepository {
	Crew save(Crew crew);

	boolean existsByName(String name);

	Optional<Crew> findById(UUID crewId);

}
