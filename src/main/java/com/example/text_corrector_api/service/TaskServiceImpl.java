package com.example.text_corrector_api.service;

import com.example.text_corrector_api.dao.api.TaskRepository;
import com.example.text_corrector_api.dto.TaskCreateDto;
import com.example.text_corrector_api.dto.TaskCreatedResponse;
import com.example.text_corrector_api.dto.TaskResponseDto;
import com.example.text_corrector_api.service.api.TaskService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public TaskCreatedResponse createTask(TaskCreateDto taskCreateDto) {
        return null;
    }

    @Override
    public TaskResponseDto getById(UUID id) {
        return null;
    }
}
