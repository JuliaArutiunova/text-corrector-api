package com.example.text_corrector_api.service;

import com.example.text_corrector_api.dto.TaskProcessingContext;
import com.example.text_corrector_api.service.api.TaskService;
import com.example.text_corrector_api.service.api.TextCorrectionService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TaskProcessorService {
    private final TaskService taskService;
    private final TextCorrectionService spellerClient;

    public TaskProcessorService(TaskService taskService, TextCorrectionService spellerClient) {
        this.taskService = taskService;
        this.spellerClient = spellerClient;
    }

    @Async("spellerTaskExecutor")
    public void processSingleTask(TaskProcessingContext task) {
        MDC.put("taskId", task.id().toString());
        log.debug("Starting text correction.....");
        try {
            String correctedText = spellerClient.correct(task.text(), task.language());
            taskService.completeTask(correctedText, task.id());
            log.info("Task processed successfully");
        } catch (Exception e) {
            log.error("Failed to process task" , e);
            taskService.failTask(task.id(), e.getMessage());
        } finally {
            MDC.clear();
        }

    }


}
