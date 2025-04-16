package com._42195km.msa.crew.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com._42195km.msa.common.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_crew_meeting_member")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrewMeetingMember extends BaseEntity {
	@Id
	@UuidGenerator
	private UUID id;

	@Column(name = "user_id", nullable = false)
	private UUID userId;

	@OneToMany(mappedBy = "meetingMember", orphanRemoval = true, cascade = CascadeType.ALL)
	@Builder.Default
	private List<CrewMeetingMemberMapping> crewMeetingMemberMappings = new ArrayList<>();

	public void addCrewMeetingMemberMapping(CrewMeetingMemberMapping crewMeetingMemberMapping) {
		crewMeetingMemberMappings.add(crewMeetingMemberMapping);
		crewMeetingMemberMapping.setMeetingMember(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof CrewMeetingMember that))
			return false;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
}
