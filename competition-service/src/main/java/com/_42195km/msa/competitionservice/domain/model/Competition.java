package com._42195km.msa.competitionservice.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com._42195km.msa.common.BaseEntity;
import com._42195km.msa.competitionservice.application.dto.request.CreateCompetitionCommandDto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@Table(name = "p_competition")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Competition extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "user_id")
	private UUID userId;

	private String title;

	private CompetitionType type;

	@Column(name = "reception_type")
	private ReceptionType receptionType;

	@Column(name = "participants_num")
	private Integer participantsNum;

	private Integer price;

	@OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CompetitionParticipantMapping> participantMappings = new ArrayList<>();

	public static Competition create(CreateCompetitionCommandDto commandDto){
		return Competition.builder()
			.userId(commandDto.getUserId())
			.title(commandDto.getTitle())
			.type(commandDto.getType())
			.receptionType(commandDto.getReceptionType())
			.participantsNum(commandDto.getParticipantsNum())
			.price(commandDto.getPrice())
			.build();
	}

}
