package com_42195km.msa.runningrecordservice.presentation.dto.request;

import java.sql.Timestamp;
import java.util.UUID;

import com_42195km.msa.runningrecordservice.application.dto.request.CreateRunningRecordCommandDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateRunningRecordRequestDto {
	private UUID userId;
	private double distance;
	private Timestamp timer;
	private double pace;

	public CreateRunningRecordCommandDto toCommandDto() {
		return new CreateRunningRecordCommandDto(userId, distance, timer, pace);
	}
}
