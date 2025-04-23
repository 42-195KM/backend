package com._42195km.msa.achievementservice.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com._42195km.msa.common.config.JpaAuditingConfig;
import com._42195km.msa.common.exception.GlobalExceptionHandler;

@Configuration
@Import({
	JpaAuditingConfig.class,
	GlobalExceptionHandler.class
})
public class GlobalConfig {
}
