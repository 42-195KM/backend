package com_42195km.msa.runningrecordservice.presentation.dto.response;

import java.sql.Timestamp;
import java.util.UUID;

import com_42195km.msa.runningrecordservice.domain.model.RunningRecord;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetRunningRecordResponseDto {
	private UUID id;
	private UUID userId;
	private double distance;
	private Timestamp timer;
	private double pace;

	public GetRunningRecordResponseDto(RunningRecord runningRecord) {
		this.id = runningRecord.getId();
		this.userId = runningRecord.getUserId();
		this.distance = runningRecord.getDistance();
		this.timer = runningRecord.getTimer();
		this.pace = runningRecord.getPace();
	}
}
