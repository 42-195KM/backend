package com._42195km.msa.achievementservice.domain.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import com._42195km.msa.common.BaseEntity;

import com._42195km.msa.achievementservice.application.dto.request.CreateAchievementCommandDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@Table(name = "p_achievement")
@AllArgsConstructor
@NoArgsConstructor
public class Achievement extends BaseEntity {
	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "id", nullable = false)
	private UUID id;

	@Column(name = "title", nullable = false, unique = true)
	private String title;

	@Column(name = "description", nullable = false)
	private String description;

	@Column(name = "criteria", nullable = false)
	private String criteria;

	@Column(name = "criteria_value", nullable = false)
	private double criteriaValue;

	@Enumerated(EnumType.STRING)
	@Column(name = "criteria_inequality", nullable = false, columnDefinition = "CriteriaInequality default 'EQUAL'")
	@JdbcTypeCode(SqlTypes.NAMED_ENUM)
	private CriteriaInequality criteriaInequality;

	@OneToMany(mappedBy = "achievement", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<AchievementUser> achievementUsers;

	public static Achievement createAchievement(CreateAchievementCommandDto createAchievementCommandDto) {
		return Achievement.builder()
			.title(createAchievementCommandDto.getTitle())
			.description(createAchievementCommandDto.getDescription())
			.criteria(createAchievementCommandDto.getCriteria())
			.criteriaValue(createAchievementCommandDto.getCriteriaValue())
			.criteriaInequality(CriteriaInequality.valueOf(createAchievementCommandDto.getCriteriaType()))
			.achievementUsers(new HashSet<>())
			.build();
	}

	public Set<AchievementUser> getAchievementUsers() {
		return achievementUsers.stream().filter(achievementUser -> {
			return !achievementUser.isDeleted();
		}).collect(Collectors.toSet());
	}
}
