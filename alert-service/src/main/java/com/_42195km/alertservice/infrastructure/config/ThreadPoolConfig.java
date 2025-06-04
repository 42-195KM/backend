package com._42195km.alertservice.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfig {
    @Bean("slackApiExecutor")
    public ExecutorService slackApiExecutor() {
        return Executors.newFixedThreadPool(50);
    }
}
