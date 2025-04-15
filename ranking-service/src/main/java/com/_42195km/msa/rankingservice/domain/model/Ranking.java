package com._42195km.msa.rankingservice.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com._42195km.msa.common.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_ranking", schema = "rankingschema")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Ranking extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false)
	private UUID identifierId;

	@Column
	@Enumerated(EnumType.STRING)
	private DomainType domainType;

	@OneToMany(mappedBy = "ranking", cascade = CascadeType.ALL, orphanRemoval = true)
	// 부모 객체에서 자식 객체 직렬화 -> 양방향 매핑 무한 참조 차단 -> Dto로 만들어서 제공...으로 수정
	// @JsonBackReference
	private List<RankingDetail> details = new ArrayList<>();

	@Builder
	public Ranking(
		UUID identifierId,
		DomainType domainType,
		List<RankingDetail> details
	) {
		this.identifierId = identifierId;
		this.domainType = domainType;
		this.details = details;
	}
}
