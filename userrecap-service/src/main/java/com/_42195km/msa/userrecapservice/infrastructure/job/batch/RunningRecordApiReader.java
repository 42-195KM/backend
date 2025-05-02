package com._42195km.msa.userrecapservice.infrastructure.job.batch;

import java.time.LocalDateTime;
import java.time.YearMonth;
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

	public RunningRecordApiReader(String targetMonth, RunningRecordClient runningRecordClient) {
		setDateCondition(targetMonth);
		this.runningRecordClient = runningRecordClient;
	}

	public void setDateCondition(String targetMonth) {
		YearMonth yearMonth = YearMonth.parse(targetMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
		this.startDate = yearMonth.atDay(1).atStartOfDay();
		this.endDate = this.startDate.with(TemporalAdjusters.lastDayOfMonth());
		log.info("paramNme = targetMonth paramValue = {}", targetMonth);
	}

	@Override
	public GetRunningRecordAppResponseDto read() throws
		UnexpectedInputException,
		ParseException,
		NonTransientResourceException {
		if (runningRecordList.isEmpty()) {
			runningRecordList.addAll(
				runningRecordClient.findAllRunningRecords(startDate, endDate)
			);
		}

		if (nextIndex < runningRecordList.size()) {
			log.info("read index: {}", nextIndex);
			return runningRecordList.get(nextIndex++);
		}

		return null;
	}
}
