package com.example.text_corrector_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;


@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "spellerTaskExecutor")
    public AsyncTaskExecutor virtualThreadTaskExecutor() {
        return new VirtualThreadTaskExecutor("speller-worker-");
    }
}
