package com._42195km.msa.userrecapservice.infrastructure.job.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com._42195km.msa.userrecapservice.application.dto.client.GetRunningRecordAppResponseDto;
import com._42195km.msa.userrecapservice.application.service.UserRecapService;
import com._42195km.msa.userrecapservice.application.service.client.RunningRecordClient;
import com._42195km.msa.userrecapservice.domain.model.strategy.SummaryType;
import com._42195km.msa.userrecapservice.infrastructure.job.batch.DataShareBean;
import com._42195km.msa.userrecapservice.infrastructure.job.batch.RunningRecordApiReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(BatchProperties.class)
@Configuration
public class PeriodicRecapJobConfig extends DefaultBatchConfiguration {
	// @Bean
	// @ConditionalOnMissingBean
	// public JobLauncherApplicationRunner jobLauncherApplicationRunner(JobLauncher jobLauncher,
	// 	JobExplorer jobExplorer, JobRepository jobRepository, BatchProperties batchProperties) {
	// 	JobLauncherApplicationRunner runner = new JobLauncherApplicationRunner(
	// 		jobLauncher, jobExplorer, jobRepository
	// 	);
	// 	String jobName = batchProperties.getJob().getName();
	// 	runner.setJobName(jobName);
	// 	return runner;
	// }

	@Bean
	public Job recapJob(Step apiCallStep, Flow recapParallelFlowStep, JobRepository jobRepository) {
		Flow apiCallFlowStep = new FlowBuilder<SimpleFlow>("flowApiCallStep")
			.start(apiCallStep)
			.build();

		return new JobBuilder("recapJob", jobRepository)
			.incrementer(new RunIdIncrementer())
			.start(apiCallFlowStep)
			.on("COMPLETED")
			.to(recapParallelFlowStep)
			.end()
			.build();
	}

	@Bean
	@JobScope
	public Flow recapParallelFlowStep(UserRecapService userRecapService,
		@Qualifier("recapTaskExecutor")
		TaskExecutor recapTaskExecutor,
		JobRepository jobRepository,
		DataShareBean<List<GetRunningRecordAppResponseDto>> dataShareBean) {

		List<TaskletStep> steps = Arrays.stream(SummaryType.values())
			.map(summaryType -> new StepBuilder(summaryType.getName() + "_summaryStep", jobRepository)
				.tasklet((contribution, chunkContext) -> {
					userRecapService.createUserRecap(summaryType, dataShareBean.getData("runningRecordData"));
					return RepeatStatus.FINISHED;
				}, getTransactionManager())
				.build())
			.toList();

		return new FlowBuilder<SimpleFlow>("recapParallelFlowStep")
			.split(recapTaskExecutor)
			.add(steps.stream()
				.map(taskletStep -> new FlowBuilder<Flow>(taskletStep.getName()).from(taskletStep).end())
				.toArray(Flow[]::new))
			.build();
	}

	@Bean
	@JobScope
	public Step apiCallStep(
		JobRepository jobRepository,
		ItemReader<GetRunningRecordAppResponseDto> runningRecordItemReader,
		ItemWriter<GetRunningRecordAppResponseDto> runningRecordItemWriter
	) {
		return new StepBuilder("apiCallStep", jobRepository)
			.<GetRunningRecordAppResponseDto, GetRunningRecordAppResponseDto>chunk(50, getTransactionManager())
			.reader(runningRecordItemReader)
			.writer(runningRecordItemWriter)
			.build();
	}

	@Bean
	@StepScope
	public RunningRecordApiReader runningRecordItemReader(
		@Value("#{jobParameters['targetMonth']}") String targetMonth,
		RunningRecordClient client) {
		log.info("targetMonth: {}", targetMonth);
		return new RunningRecordApiReader(targetMonth, client);
	}

	@Bean
	@StepScope
	public ItemWriter<GetRunningRecordAppResponseDto> runningRecordItemWriter(
		DataShareBean<List<GetRunningRecordAppResponseDto>> dataShareBean
	) {
		return items -> {
			List<GetRunningRecordAppResponseDto> data = Optional.ofNullable(
					dataShareBean.getData("runningRecordData"))
				.orElseGet(ArrayList::new);

			data.addAll(items.getItems());
			dataShareBean.putData("runningRecordData", data);
		};
	}

	@Bean(name = "recapTaskExecutor")
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(10);
		executor.setThreadNamePrefix("recap-job-thread-");
		executor.setWaitForTasksToCompleteOnShutdown(Boolean.TRUE);
		executor.initialize();
		return executor;
	}
}
