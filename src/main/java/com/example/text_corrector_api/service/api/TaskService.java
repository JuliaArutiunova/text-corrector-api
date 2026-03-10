package com.example.text_corrector_api.service.api;

import com.example.text_corrector_api.dto.TaskCreateDto;
import com.example.text_corrector_api.dto.TaskCreatedResponse;
import com.example.text_corrector_api.dto.TaskProcessingContext;
import com.example.text_corrector_api.dto.TaskResponseDto;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    TaskCreatedResponse createTask(TaskCreateDto taskCreateDto);
    TaskResponseDto getById(UUID id);
    List<TaskProcessingContext> findAndLockTasks(int limit);

    void completeTask(String correctedText, UUID taskId);

    void failTask(UUID taskId, String errorMessage);
}
