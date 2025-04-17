package com._42195km.msa.crew.domain.model;

import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com._42195km.msa.common.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
@Table(name = "p_crew_meeting_member_mapping")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CrewMeetingMemberMapping extends BaseEntity {
	@Id
	@UuidGenerator
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "crew_meeting_id", nullable = false)
	@Setter
	private CrewMeeting meeting;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "crew_meeting_member_id", nullable = false)
	@Setter
	private CrewMeetingMember meetingMember;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 50, nullable = false)
	private MeetingMemberStatus status;

	public void manageNoShow() {
		if (this.status == MeetingMemberStatus.APPROVED) {
			this.status = MeetingMemberStatus.NOSHOW;
		}

		throw new IllegalArgumentException("변경 가능한 상태가 아닙니다");
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof CrewMeetingMemberMapping that))
			return false;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	public enum MeetingMemberStatus {
		PENDING, APPROVED, REJECTED, NOSHOW, ATTENDING;
	}
}
