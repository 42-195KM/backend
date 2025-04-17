package com._42195km.msa.crew.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "p_crew_meeting")
@Getter
@NoArgsConstructor
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
	@Setter
	private Crew crew;

	@OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CrewMeetingMemberMapping> crewMeetingMemberMappings = new ArrayList<>();

	@Builder
	public CrewMeeting(String name, LocalDateTime meetingDateTime, Integer hour, String description, String type,
		Integer capacity) {
		this.name = name;
		this.meetingDateTime = meetingDateTime;
		this.hour = hour;
		this.description = description;
		this.type = parseTypeLiteral(type);
		this.capacity = capacity;
	}

	public static boolean isRegularMeetingRequest(String type) {
		return MeetingType.REGULAR.name().equalsIgnoreCase(type);
	}

	private MeetingType parseTypeLiteral(String type) {
		try {
			return MeetingType.valueOf(type.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid meeting type: " + type);
		}
	}

	public boolean isAlreadyParticipated(UUID userId) {
		return crewMeetingMemberMappings.stream()
			.anyMatch(
				crewMeetingMemberMapping -> crewMeetingMemberMapping.getMeetingMember().getUserId().equals(userId)
					&& crewMeetingMemberMapping.getDeletedAt() == null);
	}

	public void addCrewMeetingMemberMapping(CrewMeetingMemberMapping crewMeetingMemberMapping) {
		crewMeetingMemberMappings.add(crewMeetingMemberMapping);
		crewMeetingMemberMapping.setMeeting(this);
	}

	public boolean isRegularMeeting() {
		return type == MeetingType.REGULAR;
	}

	public boolean isFull() {
		return crewMeetingMemberMappings.stream()
			.filter(crewMeetingMemberMapping -> crewMeetingMemberMapping.getDeletedAt() == null)
			.count() >= capacity;
	}

	public CrewMeetingMemberMapping findCrewMeetingMemberMapping(UUID meetingMemberId) {
		return crewMeetingMemberMappings.stream()
			.filter(crewMeetingMemberMapping -> crewMeetingMemberMapping.getId().equals(meetingMemberId))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("해당 사용자는 크루 모임에 가입되어 있지 않습니다."));
	}

	public void deleteCrewMemberMappings() {
		this.crewMeetingMemberMappings.forEach(CrewMeetingMemberMapping::setDeleted);
	}

	public void removeMeetingMember(UUID meetingMemberId) {
		CrewMeetingMemberMapping crewMeetingMemberMapping = findCrewMeetingMemberMapping(meetingMemberId);
		crewMeetingMemberMapping.setDeleted();
	}

	public void update(String name, Integer hour, String description, Integer capacity) {
		if (name != null) {
			this.name = name;
		}
		if (hour != null) {
			this.hour = hour;
		}
		if (description != null) {
			this.description = description;
		}
		if (capacity != null) {
			this.capacity = capacity;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof CrewMeeting that))
			return false;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	public enum MeetingType {
		REGULAR, IRREGULAR
	}
}
