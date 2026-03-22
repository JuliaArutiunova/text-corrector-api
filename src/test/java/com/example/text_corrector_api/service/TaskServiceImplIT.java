package com.example.text_corrector_api.service;

import com.example.text_corrector_api.BaseIntegrationTest;
import com.example.text_corrector_api.dao.api.TaskRepository;
import com.example.text_corrector_api.dao.entity.TaskEntity;
import com.example.text_corrector_api.dto.TaskCreateDto;
import com.example.text_corrector_api.dto.TaskCreatedResponse;
import com.example.text_corrector_api.dto.TaskProcessingContext;
import com.example.text_corrector_api.dto.TaskResponseDto;
import com.example.text_corrector_api.enums.Language;
import com.example.text_corrector_api.enums.TaskStatus;
import com.example.text_corrector_api.exception.TaskNotFoundException;
import com.example.text_corrector_api.service.api.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class TaskServiceImplIT extends BaseIntegrationTest {
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskRepository taskRepository;

    @Test
    @DisplayName("Should successfully save task to database and return valid ID")
    void shouldCreateTask() {
        String originalText = "Hello, this is a test task for Yandex Speller.";
        Language language = Language.EN;
        TaskCreateDto dto = new TaskCreateDto(originalText, language);

        TaskCreatedResponse response = taskService.createTask(dto);
        UUID generatedId = response.id();

        assertThat(generatedId).isNotNull();

        taskRepository.flush();

        Optional<TaskEntity> savedTaskOpt = taskRepository.findById(generatedId);

        assertThat(savedTaskOpt).isPresent();
        TaskEntity savedTask = savedTaskOpt.get();

        assertThat(savedTask.getOriginalText()).isEqualTo(originalText);
        assertThat(savedTask.getLanguage()).isEqualTo(language);
        assertThat(savedTask.getStatus()).isEqualTo(TaskStatus.NEW);

        assertThat(savedTask.getCreatedAt()).isNotNull();
        assertThat(savedTask.getUpdatedAt()).isNotNull();
    }


    @Test
    @DisplayName("Should find new tasks, lock them and update status to IN_PROGRESS")
    void shouldFindAndLockTasks() {
        saveTaskWithText("Task 1");
        saveTaskWithText("Task 2");
        saveTaskWithText("Task 3");

        List<TaskProcessingContext> lockedTasks = taskService.findAndLockTasks(2);

        assertThat(lockedTasks).hasSize(2);

        List<TaskEntity> allTasks = taskRepository.findAll();

        long inProgressCount = allTasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS)
                .count();
        long newCount = allTasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.NEW)
                .count();

        assertThat(inProgressCount).isEqualTo(2);
        assertThat(newCount).isEqualTo(1);
    }


    @Test
    @DisplayName("Should return empty list when no NEW tasks are available")
    void shouldReturnEmptyListWhenNoNewTasks() {
        List<TaskProcessingContext> resultEmptyDb = taskService.findAndLockTasks(10);

        assertThat(resultEmptyDb)
                .as("Should return empty list for empty database")
                .isEmpty();

        saveTaskWithStatus(TaskStatus.IN_PROGRESS);
        saveTaskWithStatus(TaskStatus.COMPLETED);
        saveTaskWithStatus(TaskStatus.ERROR);


        List<TaskProcessingContext> resultNoNewTasks = taskService.findAndLockTasks(10);

        assertThat(resultNoNewTasks)
                .as("Should return empty list when only non-NEW tasks exist")
                .isEmpty();

        assertThat(taskRepository.findAll())
                .hasSize(3)
                .allMatch(task -> task.getStatus() != TaskStatus.NEW);
    }

    @Test
    @DisplayName("Should return task DTO when searching by existing ID")
    void shouldReturnTaskWhenIdExists() {
        TaskEntity entity = new TaskEntity();
        entity.setOriginalText("Find me");
        entity.setLanguage(Language.EN);
        entity.setStatus(TaskStatus.NEW);
        TaskEntity saved = taskRepository.saveAndFlush(entity);

        TaskResponseDto result = taskService.getById(saved.getId());

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(saved.getId());

    }

    @Test
    @DisplayName("Should throw TaskNotFoundException when ID does not exist")
    void shouldThrowExceptionWhenIdNotFound() {
        assertThatThrownBy(() -> taskService.getById(UUID.randomUUID()))
                .isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    @DisplayName("Should update status to COMPLETED and set corrected text")
    void shouldCompleteTaskSuccessfully() {
        TaskEntity entity = new TaskEntity();
        entity.setOriginalText("Errorr");
        entity.setLanguage(Language.EN);
        entity.setStatus(TaskStatus.IN_PROGRESS);
        TaskEntity saved = taskRepository.saveAndFlush(entity);
        String correctedText = "Error";

        taskService.completeTask(correctedText, saved.getId());
        taskRepository.flush();

        TaskEntity updated = taskRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(TaskStatus.COMPLETED);
        assertThat(updated.getCorrectedText()).isEqualTo(correctedText);
        assertThat(updated.getUpdatedAt()).isAfter(updated.getCreatedAt());
    }

    @Test
    @DisplayName("Should update status to ERROR and save error message")
    void shouldMarkTaskAsFailed() {
        TaskEntity entity = new TaskEntity();
        entity.setOriginalText("Some text");
        entity.setLanguage(Language.RU);
        entity.setStatus(TaskStatus.IN_PROGRESS);
        TaskEntity saved = taskRepository.saveAndFlush(entity);
        String errorMessage = "Yandex API timeout";

        taskService.failTask(saved.getId(), errorMessage);
        taskRepository.flush();

        TaskEntity updated = taskRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(TaskStatus.ERROR);
        assertThat(updated.getErrorMessage()).isEqualTo(errorMessage);
        assertThat(updated.getUpdatedAt()).isAfter(updated.getCreatedAt());
    }

    private void saveTaskWithStatus(TaskStatus status) {
        TaskEntity entity = new TaskEntity();
        entity.setOriginalText("Sample text");
        entity.setLanguage(Language.EN);
        entity.setStatus(status);
        taskRepository.saveAndFlush(entity);
    }
    private void saveTaskWithText(String text) {
        TaskEntity entity = new TaskEntity();
        entity.setOriginalText(text);
        entity.setLanguage(Language.EN);
        entity.setStatus(TaskStatus.NEW);
        taskRepository.saveAndFlush(entity);
    }
}


