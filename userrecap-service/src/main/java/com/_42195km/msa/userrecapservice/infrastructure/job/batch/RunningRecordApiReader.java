package com._42195km.msa.userrecapservice.infrastructure.job.batch;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import com._42195km.msa.userrecapservice.application.dto.client.GetRunningRecordAppResponseDto;
import com._42195km.msa.userrecapservice.application.service.client.RunningRecordClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RunningRecordApiReader implements ItemReader<GetRunningRecordAppResponseDto> {
	private final RunningRecordClient runningRecordClient;
	private final List<GetRunningRecordAppResponseDto> runningRecordList = new ArrayList<>();
	private int nextIndex = 0;

	private LocalDateTime startDate;
	private LocalDateTime endDate;

	public RunningRecordApiReader(String startDate, RunningRecordClient runningRecordClient) {
		setDateCondition(startDate);
		this.runningRecordClient = runningRecordClient;
	}

	public void setDateCondition(String startDate) {
		this.startDate = LocalDateTime.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		this.endDate = this.startDate.with(TemporalAdjusters.lastDayOfMonth());
	}

	@Override
	public GetRunningRecordAppResponseDto read() throws
		UnexpectedInputException,
		ParseException,
		NonTransientResourceException {
		if (runningRecordList.isEmpty()) {
			for (int i = 0; i < endDate.getDayOfMonth() - startDate.getDayOfMonth() + 1; i++) {
				runningRecordList.addAll(runningRecordClient.findAllRunningRecords(
					startDate, startDate.plusDays(1))
				);
			}
		}

		if (nextIndex < runningRecordList.size()) {
			log.info("read index: {}", nextIndex);
			return runningRecordList.get(nextIndex++);
		}

		return null;
	}
}
