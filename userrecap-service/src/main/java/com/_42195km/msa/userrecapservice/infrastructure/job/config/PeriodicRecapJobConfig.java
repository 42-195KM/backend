package com._42195km.msa.userrecapservice.infrastructure.job.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com._42195km.msa.userrecapservice.application.dto.client.GetRunningRecordAppResponseDto;
import com._42195km.msa.userrecapservice.application.service.UserRecapService;
import com._42195km.msa.userrecapservice.application.service.client.RunningRecordClient;
import com._42195km.msa.userrecapservice.domain.model.strategy.SummaryType;
import com._42195km.msa.userrecapservice.infrastructure.job.batch.RunningRecordApiReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class PeriodicRecapJobConfig extends DefaultBatchConfiguration {

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
		TaskExecutor recapTaskExecutor, JobRepository jobRepository,
		@Value("#{jobExecutionContext['runningRecordData']}") List<GetRunningRecordAppResponseDto> data) {
		List<TaskletStep> steps = Arrays.stream(SummaryType.values())
			.map(summaryType -> new StepBuilder(summaryType.getName() + "_summaryStep", jobRepository)
				.tasklet((contribution, chunkContext) -> {
					userRecapService.createUserRecap(summaryType, data);
					log.info("summaryType 보고서 생성 배치: {}, {}", summaryType, Thread.currentThread().getName());
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
		ItemWriter<GetRunningRecordAppResponseDto> runningRecordItemWriter,
		ExecutionContextPromotionListener promotionListener
	) {
		return new StepBuilder("apiCallStep", jobRepository)
			.<GetRunningRecordAppResponseDto, GetRunningRecordAppResponseDto>chunk(50, getTransactionManager())
			.reader(runningRecordItemReader)
			.writer(runningRecordItemWriter)
			.listener(promotionListener)
			.build();
	}

	@Bean
	@StepScope
	public RunningRecordApiReader runningRecordItemReader(
		@Value("#{jobParameters['startDate']}")
		String startDate, RunningRecordClient client) {
		return new RunningRecordApiReader(startDate, client);
	}

	@Bean
	@StepScope
	public ItemWriter<GetRunningRecordAppResponseDto> runningRecordItemWriter(
		@Value("#{stepExecution}") StepExecution stepExecution
	) {
		return items -> {
			ExecutionContext executionContext = stepExecution.getExecutionContext();

			@SuppressWarnings("unchecked")
			List<GetRunningRecordAppResponseDto> dataChunk = (List<GetRunningRecordAppResponseDto>)
				executionContext.get("runningRecordData");

			if (dataChunk == null) {
				dataChunk = new ArrayList<>();
			}

			dataChunk.addAll(items.getItems());
			executionContext.put("runningRecordData", dataChunk);

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

	@Bean
	public ExecutionContextPromotionListener promotionListener() {
		ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
		listener.setKeys(new String[] {"runningRecordData"});
		return listener;
	}
}
