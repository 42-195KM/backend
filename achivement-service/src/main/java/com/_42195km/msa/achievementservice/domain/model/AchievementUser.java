package com._42195km.msa.achievementservice.domain.model;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com._42195km.msa.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "p_achievement_user")
@AllArgsConstructor
@NoArgsConstructor
public class AchievementUser extends BaseEntity {
	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "id", nullable = false)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "achievement_id", nullable = false)
	private Achievement achievement;

	@Column(name = "user_id", nullable = false)
	private UUID userId;
}
