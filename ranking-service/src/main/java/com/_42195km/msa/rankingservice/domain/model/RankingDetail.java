package com._42195km.msa.rankingservice.domain.model;

import java.time.LocalDate;
import java.util.UUID;

import com._42195km.msa.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table(name = "p_ranking_detail", schema = "rankingschema")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RankingDetail extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ranking_id", nullable = false)
	private Ranking ranking;

	@Column(nullable = false)
	private String metricName;

	@Column(nullable = false)
	private String metricValue;

	@Column(nullable = false)
	private int rank;

	@Column(nullable = false)
	private LocalDate date;

	public void applyRanking(int rank) {
		this.rank = rank;
	}
}
