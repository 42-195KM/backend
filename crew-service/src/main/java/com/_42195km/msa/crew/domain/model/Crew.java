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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_crew")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Crew extends BaseEntity {
	@Id
	@UuidGenerator
	private UUID id;

	@Column(name = "name", length = 100, nullable = false, unique = true)
	private String name;

	@Column(name = "description", length = 100)
	private String description;

	@Column(name = "captain_id", nullable = false)
	private UUID captainId;

	@Column(name = "capacity", nullable = false)
	private Integer capacity;

	@Column(name = "is_autoAgree")
	private Boolean isAutoAgree;

	@OneToMany(mappedBy = "crew", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<CrewMemberMapping> crewMemberMappings = new ArrayList<>();

	@OneToMany(mappedBy = "crew", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<CrewMeeting> crewMeetings = new ArrayList<>();

	public void addCrewMemberMapping(CrewMemberMapping crewMemberMapping) {
		crewMemberMappings.add(crewMemberMapping);
		crewMemberMapping.setCrew(this);

	}

	public void addCrewMeeting(CrewMeeting crewMeeting) {
		crewMeetings.add(crewMeeting);
		crewMeeting.setCrew(this);

	}

	public boolean isFull() {
		return crewMemberMappings.size() >= capacity;
	}

	public boolean isAlreadyJoined(UUID userId) {
		return crewMemberMappings.stream()
			.anyMatch(crewMemberMapping -> crewMemberMapping.getCrewMember().getUserId().equals(userId)
				&& crewMemberMapping.isAlreadyJoined());
	}

	public boolean isInBlackList(UUID userId) {
		return crewMemberMappings.stream()
			.anyMatch(crewMemberMapping -> crewMemberMapping.getCrewMember().getUserId().equals(userId)
				&& crewMemberMapping.isInBlackList());
	}

	public void update(String description, Integer capacity, Boolean isAutoAgree) {
		if (description != null) {
			this.description = description;
		}
		if (capacity != null) {
			this.capacity = capacity;
		}
		if (isAutoAgree != null) {
			this.isAutoAgree = isAutoAgree;
		}

	}

	public void setDeletedCrewMeetings() {
		if (!crewMeetings.isEmpty()) {
			crewMeetings.forEach(CrewMeeting::setDeleted);
		}
	}

	public void setDeletedCrewMember() {
		List<CrewMember> members = crewMemberMappings.stream().map(CrewMemberMapping::getCrewMember).toList();

		if (!members.isEmpty()) {
			members.forEach(CrewMember::setDeleted);
		}

		if (!crewMemberMappings.isEmpty()) {
			crewMemberMappings.forEach(CrewMemberMapping::setDeleted);
		}
	}

	public boolean isNotCaptain(UUID userId) {
		return !isCaptain(userId);
	}

	public boolean isCaptain(UUID userId) {
		return captainId.equals(userId);
	}

	public CrewMemberMapping approve(UUID userId) {
		CrewMemberMapping memberMapping = findCrewMemberMappingByUserId(userId);

		memberMapping.approve();

		return memberMapping;
	}

	public CrewMemberMapping reject(UUID userId) {
		CrewMemberMapping memberMapping = findCrewMemberMappingByUserId(userId);
		memberMapping.reject();

		return memberMapping;
	}

	public boolean isNotMember(UUID userId) {
		return crewMemberMappings.stream()
			.noneMatch(crewMemberMapping -> crewMemberMapping.getCrewMember().getUserId().equals(userId)
				&& crewMemberMapping.getDeletedAt() == null);
	}

	public CrewMemberMapping expel(UUID memberId) {
		CrewMemberMapping memberMapping = findCrewMemberMappingByUserId(memberId);
		memberMapping.expel();

		return memberMapping;
	}

	public void removeCrewMemberMapping(UUID memberId) {
		CrewMemberMapping crewMemberMapping = findCrewMemberMappingByUserId(memberId);
		crewMemberMappings.remove(crewMemberMapping);
		crewMemberMapping.setCrew(null);
	}

	public CrewMemberMapping findCrewMemberMappingByUserId(UUID userId) {
		return this.crewMemberMappings.stream()
			.filter(m -> m.getCrewMember().getUserId().equals(userId) && m.getDeletedAt() == null)
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("해당 사용자는 크루에 가입되어 있지 않습니다."));
	}

	public CrewMeeting findCrewMeeting(UUID meetingId) {
		return this.crewMeetings.stream()
			.filter(m -> m.getId().equals(meetingId) && m.getDeletedAt() == null)
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("해당 모임은 크루에 존재하지 않습니다."));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Crew crew))
			return false;
		return Objects.equals(id, crew.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
}
