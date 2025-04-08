package com_42195km.msa.runningrecordservice.domain.model;

import java.sql.Timestamp;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com._42195km.msa.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "p_ running_record")
public class RunningRecord extends BaseEntity {
	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "id", nullable = false)
	private UUID id;

	@Column(name = "user_id", nullable = false)
	private UUID userId;

	@Column(name = "distance", nullable = false)
	private double distance;

	@Column(name = "timer", nullable = false)
	private Timestamp timer;

	@Column(name = "pace", nullable = false)
	private double pace;
}
