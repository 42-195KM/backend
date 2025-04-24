package com._42195km.msa.chatbotservice.infrastructure.config;

import com.querydsl.core.annotations.Config;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class AsyncConfig {
    @Bean
    public ThreadPoolTaskExecutor asyncThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setTaskDecorator(new CopyTaskDecorator());
        return executor;
    }

    private static class CopyTaskDecorator implements TaskDecorator {
        @Override
        public Runnable decorate(Runnable runnable) {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return () -> {
                try {
                    // 새 스레드에 이전 컨텍스트를 설정
                    RequestContextHolder.setRequestAttributes(attributes);
                    runnable.run();
                } finally {
                    // 꼭 클리어해줘야 메모리 누수 방지
                    RequestContextHolder.resetRequestAttributes();
                }
            };

        }
    }

}
