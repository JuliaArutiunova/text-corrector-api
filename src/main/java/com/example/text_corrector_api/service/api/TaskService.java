package com.example.text_corrector_api.service.api;

import com.example.text_corrector_api.dto.TaskCreateDto;
import com.example.text_corrector_api.dto.TaskCreatedResponse;
import com.example.text_corrector_api.dto.TaskResponseDto;

import java.util.UUID;

public interface TaskService {
    TaskCreatedResponse createTask(TaskCreateDto taskCreateDto);
    TaskResponseDto getById(UUID id);
}
