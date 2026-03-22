package com.example.text_corrector_api.dao.api;

import com.example.text_corrector_api.BaseIntegrationTest;
import com.example.text_corrector_api.dao.entity.TaskEntity;
import com.example.text_corrector_api.enums.Language;
import com.example.text_corrector_api.enums.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskRepositoryIT extends BaseIntegrationTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    @DisplayName("Should only select tasks with the NEW status")
    void shouldSelectOnlyNewTasks() {
        TaskEntity newTask = createTask("New task", TaskStatus.NEW, Language.EN);
        TaskEntity inProgressTask = createTask("In progress", TaskStatus.IN_PROGRESS, Language.EN);
        TaskEntity completedTask = createTask("Completed", TaskStatus.COMPLETED, Language.EN);
        TaskEntity errorTask = createTask("Error", TaskStatus.ERROR, Language.RU);

        taskRepository.saveAll(List.of(newTask, inProgressTask, completedTask, errorTask));
        taskRepository.flush();

        List<TaskEntity> result = taskRepository.findNewTasksForProcessing(10);

        assertThat(result)
                .hasSize(1)
                .allMatch(task -> task.getStatus() == TaskStatus.NEW)
                .extracting(TaskEntity::getOriginalText)
                .containsExactly("New task");
    }

    @Test
    @DisplayName("Should comply with the selection limit")
    void shouldRespectLimit() {
        TaskEntity task1 = createTask("Task 1", TaskStatus.NEW, Language.EN);
        TaskEntity task2 = createTask("Task 2", TaskStatus.NEW, Language.EN);
        TaskEntity task3 = createTask("Task 3", TaskStatus.NEW, Language.EN);
        TaskEntity task4 = createTask("Task 4", TaskStatus.NEW, Language.EN);

        taskRepository.saveAll(List.of(task1, task2, task3, task4));
        taskRepository.flush();

        List<TaskEntity> resultWithLimit2 = taskRepository.findNewTasksForProcessing(2);
        List<TaskEntity> resultWithLimit5 = taskRepository.findNewTasksForProcessing(5);

        assertThat(resultWithLimit2).hasSize(2);
        assertThat(resultWithLimit5).hasSize(4); // всего 4 задачи
    }

    @Test
    @DisplayName("Should return tasks ordered by creation date (oldest first)")
    void shouldReturnTasksInOrderOfCreation() {
        TaskEntity first = createTask("First", TaskStatus.NEW, Language.EN);
        TaskEntity second = createTask("Second", TaskStatus.NEW, Language.EN);
        TaskEntity third = createTask("Third", TaskStatus.NEW, Language.EN);

        taskRepository.saveAll(List.of(first, second, third));
        taskRepository.flush();

        List<TaskEntity> result = taskRepository.findNewTasksForProcessing(10);

        assertThat(result)
                .hasSize(3)
                .extracting(TaskEntity::getOriginalText)
                .containsExactly("First", "Second", "Third");
    }

    @Test
    @DisplayName("Should return an empty list if there are no NEW tasks")
    void shouldReturnEmptyListWhenNoNewTasks() {
        TaskEntity inProgressTask = createTask("In progress", TaskStatus.IN_PROGRESS, Language.EN);
        taskRepository.save(inProgressTask);
        taskRepository.flush();

        List<TaskEntity> result = taskRepository.findNewTasksForProcessing(10);

        assertThat(result).isEmpty();
    }

    private TaskEntity createTask(String text, TaskStatus status, Language language) {
        TaskEntity task = new TaskEntity();
        task.setOriginalText(text);
        task.setStatus(status);
        task.setLanguage(language);
        return task;
    }

}