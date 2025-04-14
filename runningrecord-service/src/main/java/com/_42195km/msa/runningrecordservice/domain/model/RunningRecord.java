package com._42195km.msa.runningrecordservice.domain.model;

import java.time.Duration;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com._42195km.msa.common.BaseEntity;

import com._42195km.msa.runningrecordservice.application.dto.request.CreateRunningRecordCommandDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
@Table(name = "p_running_record")
@AllArgsConstructor
@NoArgsConstructor
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
	private Duration timer;

	@Column(name = "pace", nullable = false)
	private double pace;

	public static RunningRecord createRunningRecord(CreateRunningRecordCommandDto createRunningRecordCommandDto) {
		return RunningRecord.builder()
			.userId(createRunningRecordCommandDto.getUserId())
			.distance(createRunningRecordCommandDto.getDistance())
			.timer(createRunningRecordCommandDto.getTimer())
			.pace(createRunningRecordCommandDto.getPace())
			.build();
	}
}
