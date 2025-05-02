package com._42195km.msa.userrecapservice.infrastructure.job.scheduling;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecapJobScheduler {
	private final JobLauncher jobLauncher;
	private final Job recapJob;

	@Scheduled(cron = "0 0 0 * * *")
	public void runRecapJob() {
		try {
			String targetMonth = LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth()).toString().substring(0, 7);
			JobParameters jobParameters = new JobParametersBuilder()
				.addString("targetMonth", targetMonth)
				.addLong("time", System.currentTimeMillis())
				.toJobParameters();
			jobLauncher.run(recapJob, jobParameters);
		} catch (Exception e) {
			log.error("RecapJobScheduler error: {}", e.getMessage());
		}
	}

}
