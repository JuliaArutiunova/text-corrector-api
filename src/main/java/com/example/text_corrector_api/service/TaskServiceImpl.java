package com.example.text_corrector_api.service;

import com.example.text_corrector_api.dao.api.TaskRepository;
import com.example.text_corrector_api.dao.entity.TaskEntity;
import com.example.text_corrector_api.dto.TaskCreateDto;
import com.example.text_corrector_api.dto.TaskCreatedResponse;
import com.example.text_corrector_api.dto.TaskProcessingContext;
import com.example.text_corrector_api.dto.TaskResponseDto;
import com.example.text_corrector_api.enums.TaskStatus;
import com.example.text_corrector_api.exception.TaskNotFoundException;
import com.example.text_corrector_api.mapper.TaskMapper;
import com.example.text_corrector_api.service.api.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    @Transactional
    public TaskCreatedResponse createTask(TaskCreateDto taskCreateDto) {
        TaskEntity taskEntity = taskMapper.toTaskEntity(taskCreateDto);
        taskEntity = taskRepository.save(taskEntity);
        return new TaskCreatedResponse(taskEntity.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponseDto getById(UUID id) {
        TaskEntity taskEntity = taskRepository.findById(id).orElseThrow(() ->
                new TaskNotFoundException(id));
        return taskMapper.toTaskResponseDto(taskEntity);
    }

    @Override
    @Transactional
    public List<TaskProcessingContext> findAndLockTasks(int limit) {
        List<TaskEntity> tasks = taskRepository.findNewTasksForProcessing(limit);

        return tasks.stream().map(taskEntity -> {
            taskEntity.setStatus(TaskStatus.IN_PROGRESS);
            log.info("Task {} is now IN_PROGRESS", taskEntity.getId());
            return taskMapper.toTaskProcessingContext(taskEntity);
        }).toList();
    }


    @Override
    @Transactional
    public void completeTask(String correctedText, UUID taskId) {
        taskRepository.findById(taskId).ifPresentOrElse(taskEntity -> {
                    taskEntity.setCorrectedText(correctedText);
                    taskEntity.setStatus(TaskStatus.COMPLETED);
                }, () -> log.error("Failed to complete task {}: record not found", taskId));

    }

    @Override
    @Transactional
    public void failTask(UUID taskId, String errorMessage) {
        taskRepository.findById(taskId).ifPresentOrElse(taskEntity -> {
                    taskEntity.setStatus(TaskStatus.ERROR);
                    taskEntity.setErrorMessage(errorMessage);
                }, () -> log.error("Unable to record failure for task {}: record not found", taskId));
    }


}
