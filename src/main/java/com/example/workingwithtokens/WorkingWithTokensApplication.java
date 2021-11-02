package com.example.workingwithtokens;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableScheduling
@EnableAsync(proxyTargetClass = true)
public class WorkingWithTokensApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkingWithTokensApplication.class, args);
    }

    @Bean("schedulePool1")
    public Executor jobPool() {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(1);
        exec.setMaxPoolSize(1);
        exec.setQueueCapacity(10);
        exec.setThreadNamePrefix("first-");
        exec.initialize();
        return exec;
    }

    @Bean("schedulePool2")
    public Executor jobPool2() {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(1);
        exec.setMaxPoolSize(1);
        exec.setQueueCapacity(10);
        exec.setThreadNamePrefix("second-");
        exec.initialize();
        return exec;
    }
}

