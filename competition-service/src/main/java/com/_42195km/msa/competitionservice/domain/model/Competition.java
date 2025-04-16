package com._42195km.msa.competitionservice.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com._42195km.msa.common.BaseEntity;
import com._42195km.msa.competitionservice.application.dto.request.CreateCompetitionCommandDto;
import com._42195km.msa.competitionservice.application.dto.request.UpdateCompetitionCommandDto;

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

@Getter
@Entity
@Table(name = "p_competition")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Competition extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "user_id")
	private UUID userId;

	private String title;

	@Enumerated(EnumType.STRING)
	private CompetitionType type;

	@Column(name = "reception_type")
	@Enumerated(EnumType.STRING)
	private ReceptionType receptionType;

	@Column(name = "participants_num")
	private Integer participantsNum;

	private Integer price;

	@OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CompetitionParticipantMapping> participantMappings = new ArrayList<>();

	@Builder
	public Competition(UUID userId, String title, CompetitionType type, ReceptionType receptionType,
		Integer participantsNum, Integer price) {
		this.userId = userId;
		this.title = title;
		this.type = type;
		this.receptionType = receptionType;
		this.participantsNum = participantsNum;
		this.price = price;
	}

	public static Competition create(CreateCompetitionCommandDto commandDto) {
		return Competition.builder()
			.userId(commandDto.getUserId())
			.title(commandDto.getTitle())
			.type(commandDto.getType())
			.receptionType(commandDto.getReceptionType())
			.participantsNum(commandDto.getParticipantsNum())
			.price(commandDto.getPrice())
			.build();
	}

	public void update(UpdateCompetitionCommandDto commandDto) {
		if (commandDto.getTitle() != null) {
			this.title = commandDto.getTitle();
		}
		if (commandDto.getType() != null) {
			this.type = commandDto.getType();
		}
		if (commandDto.getReceptionType() != null) {
			this.receptionType = commandDto.getReceptionType();
		}
		if (commandDto.getParticipantsNum() != null) {
			this.participantsNum = commandDto.getParticipantsNum();
		}
		if (commandDto.getPrice() != null) {
			this.price = commandDto.getPrice();
		}
	}
}
