package com.example.text_corrector_api.service;

import com.example.text_corrector_api.dto.TaskProcessingContext;
import com.example.text_corrector_api.service.api.TaskService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CorrectionScheduler {
    private final TaskService taskService;
    private final TaskProcessorService taskProcessorService;
    @Value("${scheduler.batch-size}")
    private int batchSize;

    public CorrectionScheduler(TaskService taskService, TaskProcessorService taskProcessorService) {
        this.taskService = taskService;
        this.taskProcessorService = taskProcessorService;
    }

    @Scheduled(fixedDelayString = "${scheduler.delay}")
    public void processTasks() {
        List<TaskProcessingContext> tasks = taskService.findAndLockTasks(batchSize);
        if (tasks.isEmpty()) {
            return;
        }
        tasks.forEach(taskProcessorService::processSingleTask);


    }

}
