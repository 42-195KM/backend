package com._42195km.msa.competitionservice.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Configuration
public class MetricsConfig {

	private final MeterRegistry meterRegistry;

	public MetricsConfig(MeterRegistry meterRegistry) {
		this.meterRegistry = meterRegistry;
	}

	// 대회 신청 시작 카운터
	@Bean
	public Counter applicationStartCounter() {
		return Counter.builder("competition.application.start")
			.description("대회 신청 시작 횟수")
			.register(meterRegistry);
	}

	// 대회 신청 완료 카운터
	@Bean
	public Counter applicationCompleteCounter() {
		return Counter.builder("competition.application.complete")
			.description("대회 신청 완료 횟수")
			.register(meterRegistry);
	}

	// 대회 신청 실패 카운터
	@Bean
	public Counter applicationFailCounter() {
		return Counter.builder("competition.application.fail")
			.description("대회 신청 실패 횟수")
			.register(meterRegistry);
	}

	// 각 단계별 카운터
	@Bean
	public Counter termsAgreementCounter() {
		return Counter.builder("competition.application.step.terms")
			.description("약관 동의 단계 완료 횟수")
			.register(meterRegistry);
	}

	@Bean
	public Counter souvenirSelectionCounter() {
		return Counter.builder("competition.application.step.souvenir")
			.description("기념품 선택 단계 완료 횟수")
			.register(meterRegistry);
	}

	@Bean
	public Counter shippingAddressCounter() {
		return Counter.builder("competition.application.step.shipping")
			.description("배송지 입력 단계 완료 횟수")
			.register(meterRegistry);
	}

	@Bean
	public Counter paymentCounter() {
		return Counter.builder("competition.application.step.payment")
			.description("결제 단계 완료 횟수")
			.register(meterRegistry);
	}

	// 전체 신청 과정 소요 시간 타이머
	@Bean
	public Timer applicationTotalTimeTimer() {
		return Timer.builder("competition.application.total.time")
			.description("대회 신청 전체 과정 소요 시간")
			.publishPercentiles(0.5, 0.75, 0.95, 0.99)
			.publishPercentileHistogram()
			.register(meterRegistry);
	}

	// 단계별 소요 시간 타이머
	@Bean
	public Timer termsStepTimeTimer() {
		return Timer.builder("competition.application.step.terms.time")
			.description("약관 동의 단계 소요 시간")
			.publishPercentiles(0.5, 0.75, 0.95, 0.99)
			.register(meterRegistry);
	}

	@Bean
	public Timer souvenirStepTimeTimer() {
		return Timer.builder("competition.application.step.souvenir.time")
			.description("기념품 선택 단계 소요 시간")
			.publishPercentiles(0.5, 0.75, 0.95, 0.99)
			.register(meterRegistry);
	}

	@Bean
	public Timer shippingStepTimeTimer() {
		return Timer.builder("competition.application.step.shipping.time")
			.description("배송지 입력 단계 소요 시간")
			.publishPercentiles(0.5, 0.75, 0.95, 0.99)
			.register(meterRegistry);
	}

	@Bean
	public Timer paymentStepTimeTimer() {
		return Timer.builder("competition.application.step.payment.time")
			.description("결제 단계 소요 시간")
			.publishPercentiles(0.5, 0.75, 0.95, 0.99)
			.register(meterRegistry);
	}
}
