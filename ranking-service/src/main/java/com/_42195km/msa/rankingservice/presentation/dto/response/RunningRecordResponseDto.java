package com._42195km.msa.rankingservice.presentation.dto.response;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class RunningRecordResponseDto {

	private String code;
	private String message;
	private int status;
	private PageResponse data;

	@Builder
	@Getter
	@AllArgsConstructor
	public static class PageResponse {

		private List<RunningRecordData> content;
		private boolean last;
	}

	@Builder
	@Getter
	@AllArgsConstructor
	public static class RunningRecordData {

		private UUID id;
		private UUID userId;
		private double distance;
		private Duration timer;
		private double pace;
	}
}
