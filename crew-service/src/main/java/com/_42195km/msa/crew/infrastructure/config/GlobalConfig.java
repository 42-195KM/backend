package com._42195km.msa.crew.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com._42195km.msa.common.config.JpaAuditingConfig;

@Configuration
@Import({
	JpaAuditingConfig.class
})
public class GlobalConfig {
}
