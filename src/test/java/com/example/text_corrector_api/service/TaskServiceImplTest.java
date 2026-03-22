package com.example.text_corrector_api.service;

import com.example.text_corrector_api.dao.api.TaskRepository;
import com.example.text_corrector_api.dao.entity.TaskEntity;
import com.example.text_corrector_api.dto.TaskCreateDto;
import com.example.text_corrector_api.dto.TaskCreatedResponse;
import com.example.text_corrector_api.dto.TaskProcessingContext;
import com.example.text_corrector_api.enums.Language;
import com.example.text_corrector_api.enums.TaskStatus;
import com.example.text_corrector_api.exception.TaskNotFoundException;
import com.example.text_corrector_api.mapper.TaskMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskMapper taskMapper;
    @InjectMocks
    private TaskServiceImpl taskService;

    private final UUID taskId = UUID.randomUUID();

    @Test
    @DisplayName("Should return response with generated ID when task is successfully created")
    void shouldReturnIdWhenTaskCreated() {
        TaskCreateDto dto = new TaskCreateDto("test text", Language.EN);
        TaskEntity entity = new TaskEntity();
        ReflectionTestUtils.setField(entity, "id", taskId);

        when(taskMapper.toTaskEntity(dto)).thenReturn(entity);
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(entity);

        TaskCreatedResponse response = taskService.createTask(dto);

        assertThat(response.id()).isEqualTo(taskId);
        verify(taskRepository).save(entity);
    }

    @Test
    @DisplayName("Should throw TaskNotFoundException when searching for non-existent ID")
    void shouldThrowExceptionWhenTaskNotFound() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getById(taskId))
                .isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    @DisplayName("Should update status to IN_PROGRESS for all retrieved tasks")
    void shouldUpdateStatusToInProgressWhenTasksFound() {
        int limit = 2;
        TaskEntity task1 = new TaskEntity();
        TaskEntity task2 = new TaskEntity();
        List<TaskEntity> tasks = List.of(task1, task2);

        TaskProcessingContext mockContext = new TaskProcessingContext(UUID.randomUUID(), "text", Language.EN);

        when(taskRepository.findNewTasksForProcessing(limit)).thenReturn(tasks);
        when(taskMapper.toTaskProcessingContext(any(TaskEntity.class))).thenReturn(mockContext);

        List<TaskProcessingContext> results = taskService.findAndLockTasks(limit);

        assertThat(results).hasSize(2);
        assertThat(tasks)
                .extracting(TaskEntity::getStatus)
                .containsOnly(TaskStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("Should update status to COMPLETED and set corrected text")
    void shouldCompleteTaskWhenFound() {
        TaskEntity entity = new TaskEntity();
        entity.setStatus(TaskStatus.IN_PROGRESS);
        String correctedText = "fixed text";

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(entity));

        taskService.completeTask(correctedText, taskId);

        assertThat(entity.getStatus()).isEqualTo(TaskStatus.COMPLETED);
        assertThat(entity.getCorrectedText()).isEqualTo(correctedText);
    }

    @Test
    @DisplayName("Should update status to ERROR and save error message when task fails")
    void shouldFailTaskWhenFound() {
        TaskEntity entity = new TaskEntity();
        entity.setStatus(TaskStatus.IN_PROGRESS);
        String errorMsg = "API error";

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(entity));

        taskService.failTask(taskId, errorMsg);

        assertThat(entity.getStatus()).isEqualTo(TaskStatus.ERROR);
        assertThat(entity.getErrorMessage()).isEqualTo(errorMsg);
    }

}