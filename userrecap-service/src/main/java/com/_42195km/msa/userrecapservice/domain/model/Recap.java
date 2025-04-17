package com._42195km.msa.userrecapservice.domain.model;

import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com._42195km.msa.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_recap")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class Recap extends BaseEntity {
	@Id
	@UuidGenerator
	private UUID id;

	@Column(name = "user_id", nullable = false)
	private UUID userId;

	@Embedded
	private SummaryDetail recapDetail;

	private Recap(UUID userId, SummaryDetail recapDetail) {
		this.userId = userId;
		this.recapDetail = recapDetail;
	}

	public static Recap of(UUID userId, SummaryDetail recapDetail) {
		return new Recap(userId, recapDetail);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Recap recap))
			return false;
		return Objects.equals(id, recap.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
}
