package com_42195km.msa.runningrecordservice.application.dto.request;

import java.sql.Timestamp;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateRunningRecordCommandDto {
	private UUID userId;
	private double distance;
	private Timestamp timer;
	private double pace;
}
