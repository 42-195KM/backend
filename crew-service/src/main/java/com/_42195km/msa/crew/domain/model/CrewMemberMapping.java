package com._42195km.msa.crew.domain.model;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com._42195km.msa.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_crew_member_mapping")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CrewMemberMapping extends BaseEntity {
	@Id
	@UuidGenerator
	private UUID id;

	@ManyToOne
	@JoinColumn(name = "crew_id", nullable = false)
	private Crew crew;

	@ManyToOne
	@JoinColumn(name = "crew_member_id", nullable = false)
	private CrewMember crewMember;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 50, nullable = false)
	private CrewMemberStatus status;

	public enum CrewMemberStatus {
		PENDING, APPROVED, REJECTED, BLACKLIST
	}
}
