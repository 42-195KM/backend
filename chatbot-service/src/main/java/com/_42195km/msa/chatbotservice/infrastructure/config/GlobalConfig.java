package com._42195km.msa.chatbotservice.infrastructure.config;

import com._42195km.msa.common.config.JpaAuditingConfig;
import com._42195km.msa.common.exception.GlobalExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        JpaAuditingConfig.class,
        GlobalExceptionHandler.class
})
public class GlobalConfig {
}
