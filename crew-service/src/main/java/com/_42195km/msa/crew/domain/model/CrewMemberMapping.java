package com._42195km.msa.crew.domain.model;

import java.util.Objects;
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
import lombok.Setter;

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
	@Setter
	private Crew crew;

	@ManyToOne
	@JoinColumn(name = "crew_member_id", nullable = false)
	private CrewMember crewMember;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 50, nullable = false)
	private CrewMemberStatus status;

	public void approve() {
		if (this.status == CrewMemberStatus.PENDING) {
			this.status = CrewMemberStatus.APPROVED;
		}

		throw new IllegalStateException("이미 승인된 크루입니다.");
	}

	public void reject() {
		if (this.status == CrewMemberStatus.PENDING) {
			this.status = CrewMemberStatus.REJECTED;
		}
		throw new IllegalStateException("이미 거절된 크루입니다.");
	}

	public boolean isAlreadyJoined() {
		return this.status == CrewMemberStatus.APPROVED;
	}

	public boolean isInBlackList() {
		return this.status == CrewMemberStatus.BLACKLIST;
	}

	public void expel() {
		if (this.status == CrewMemberStatus.APPROVED) {
			this.status = CrewMemberStatus.BLACKLIST;
		}

		throw new IllegalStateException("크루원이 아닌 사용자에게 적용할 수 없습니다");
	}

	public enum CrewMemberStatus {
		PENDING, APPROVED, REJECTED, BLACKLIST
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof CrewMemberMapping that))
			return false;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
}
