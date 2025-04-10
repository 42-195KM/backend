package com._42195km.msa.competitionservice.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com._42195km.msa.competitionservice.domain.model.Competition;
import com._42195km.msa.competitionservice.domain.model.CompetitionParticipantMapping;
import com._42195km.msa.competitionservice.domain.model.Participant;

public interface CompetitionParticipantMappingJpaRepository extends JpaRepository<CompetitionParticipantMapping, UUID> {

	@Query("""
		    SELECT CASE WHEN COUNT(cpm) > 0 THEN true ELSE false END
		    FROM CompetitionParticipantMapping cpm
		    JOIN cpm.participant p
		    WHERE p.participantId = :participantId
		      AND cpm.competition.id = :competitionId
		""")
	Boolean existsByParticipantIdAndCompetitionId(@Param("participantId") UUID participantId,
		@Param("competitionId") UUID competitionId);

	Integer countByCompetitionAndParticipant(Competition competition, Participant participant);

	List<CompetitionParticipantMapping> findAllByCompetition(Competition competition);

	@Query("SELECT p.participant FROM CompetitionParticipantMapping p WHERE p.competition.id = :competitionId")
	Page<Participant> findParticipantsByCompetitionId(@Param("competitionId") UUID competitionId, Pageable pageable);

}
