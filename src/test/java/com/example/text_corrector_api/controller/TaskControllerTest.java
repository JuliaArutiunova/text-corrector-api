package com.example.text_corrector_api.controller;

import com.example.text_corrector_api.dto.TaskCreateDto;
import com.example.text_corrector_api.dto.TaskCreatedResponse;
import com.example.text_corrector_api.dto.TaskResponseDto;
import com.example.text_corrector_api.enums.ErrorCode;
import com.example.text_corrector_api.enums.Language;
import com.example.text_corrector_api.enums.TaskStatus;
import com.example.text_corrector_api.exception.TaskNotFoundException;
import com.example.text_corrector_api.service.api.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private TaskService taskService;
    @Autowired
    private ObjectMapper objectMapper;
    private final String url = "/tasks";

    @Test
    @DisplayName("Should return task id")
    void shouldReturnTaskId() throws Exception {
        TaskCreateDto taskCreateDto = new TaskCreateDto("Hello world!", Language.EN);
        TaskCreatedResponse taskCreatedResponse = new TaskCreatedResponse(UUID.randomUUID());
        when(taskService.createTask(taskCreateDto)).thenReturn(taskCreatedResponse);

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(taskCreatedResponse.id().toString()));

        verify(taskService).createTask(taskCreateDto);

    }

    @Test
    @DisplayName("Should return HTTP 400 Bad Request for invalid user creation input")
    void shouldReturnBadRequestWhenInvalidInput() throws Exception {
        TaskCreateDto taskCreateDto = new TaskCreateDto("123", Language.EN);
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskCreateDto)))
                .andExpect(jsonPath("$.errorMessage").value("Text must contain at least one letter"))
                .andExpect(jsonPath("$.errorCode").value(String.valueOf(ErrorCode.VALIDATION_ERROR.getCode())))
                .andExpect(jsonPath("$.path").value(url))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(status().isBadRequest());

        verify(taskService, times(0)).createTask(any(TaskCreateDto.class));
    }

    @Test
    @DisplayName("Should return TaskResponseDto when the task found")
    void shouldReturnTaskResponseDto() throws Exception {
        TaskResponseDto taskResponseDto = new TaskResponseDto(
                UUID.randomUUID(),
                TaskStatus.COMPLETED,
                "Hello world",
                null
        );
        when(taskService.getById(taskResponseDto.id())).thenReturn(taskResponseDto);
        mockMvc.perform(get(url + "/" + taskResponseDto.id()))
                .andExpect(jsonPath("$.id").value(taskResponseDto.id().toString()))
                .andExpect(jsonPath("$.status").value(taskResponseDto.status().name()))
                .andExpect(jsonPath("$.correctedText").value(taskResponseDto.correctedText()))
                .andExpect(jsonPath("$.errorMessage").doesNotExist())
                .andExpect(status().isOk());

        verify(taskService).getById(taskResponseDto.id());
    }

    @Test
    @DisplayName("Should return HTTP 404 when task not found")
    void shouldReturnNotFound() throws Exception {
        UUID nonExistingId = UUID.randomUUID();
        when(taskService.getById(nonExistingId)).thenThrow(new TaskNotFoundException(nonExistingId));
        mockMvc.perform(get(url + "/" + nonExistingId))
                .andExpect(jsonPath("$.errorMessage").value(String.format("Task with id: %s not found", nonExistingId)))
                .andExpect(jsonPath("$.errorCode").value(String.valueOf(ErrorCode.TASK_NOT_FOUND.getCode())))
                .andExpect(jsonPath("$.path").value(url + "/" + nonExistingId))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(status().isNotFound());

        verify(taskService).getById(nonExistingId);
    }


}