package com._42195km.msa.crew.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com._42195km.msa.common.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_crew_meeting")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrewMeeting extends BaseEntity {

	@Id
	@UuidGenerator
	private UUID id;

	@Column(name = "name", length = 100, nullable = false)
	private String name;

	@Column(name = "meeting_date_time", nullable = false)
	private LocalDateTime meetingDateTime;

	@Column(name = "hour")
	private Integer hour;

	@Column(name = "description", length = 100)
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false)
	private MeetingType type;

	@Column(name = "capacity", nullable = false)
	private Integer capacity;

	@ManyToOne
	@JoinColumn(name = "crew_id", nullable = false)
	private Crew crew;

	@OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CrewMeetingMemberMapping> crewMeetingMemberMappings = new ArrayList<>();

	public enum MeetingType {
		REGULAR, IRREGULAR
	}

	public void addCrewMeetingMemberMapping(CrewMeetingMemberMapping crewMeetingMemberMapping) {
		crewMeetingMemberMappings.add(crewMeetingMemberMapping);
		crewMeetingMemberMapping.setMeeting(this);
	}

}
