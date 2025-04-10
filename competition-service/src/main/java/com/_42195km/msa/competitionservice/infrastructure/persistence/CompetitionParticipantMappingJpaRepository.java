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

	@Query("SELECT c.id, c.title, c.type, c.receptionType, p.participantId, p.statue FROM CompetitionParticipantMapping cpm JOIN cpm.competition c JOIN cpm.participant p WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	Page<Object> searchByTitle(@Param("keyword") String keyword, Pageable pageable);

	@Query("SELECT c.id, c.title, c.type, c.receptionType, p.participantId, p.statue FROM CompetitionParticipantMapping cpm JOIN cpm.competition c JOIN cpm.participant p WHERE CAST(c.type AS string) = :keyword")
	Page<Object> searchByCompetitionType(@Param("keyword") String keyword, Pageable pageable);

	@Query("SELECT c.id, c.title, c.type, c.receptionType, p.participantId, p.statue FROM CompetitionParticipantMapping cpm JOIN cpm.competition c JOIN cpm.participant p WHERE CAST(c.receptionType AS string) = :keyword")
	Page<Object> searchByReceptionType(@Param("keyword") String keyword, Pageable pageable);

	@Query("SELECT c.id, c.title, c.type, c.receptionType, p.participantId, p.statue FROM CompetitionParticipantMapping cpm JOIN cpm.competition c JOIN cpm.participant p WHERE CAST(p.statue AS string) = :keyword")
	Page<Object> searchByStatue(@Param("keyword") String keyword, Pageable pageable);

	@Query("SELECT c.id, c.title, c.type, c.receptionType, p.participantId, p.statue " +
		"FROM CompetitionParticipantMapping cpm " +
		"JOIN cpm.competition c " +
		"JOIN cpm.participant p " +
		"WHERE cast(c.id as string) = :uuid OR cast(p.participantId as string) = :uuid")
	Page<Object> searchByUuid(@Param("uuid") UUID uuid, Pageable pageable);
}
