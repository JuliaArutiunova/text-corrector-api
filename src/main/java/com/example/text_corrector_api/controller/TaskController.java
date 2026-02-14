package com.example.text_corrector_api.controller;

import com.example.text_corrector_api.dto.TaskCreateDto;
import com.example.text_corrector_api.dto.TaskCreatedResponse;
import com.example.text_corrector_api.dto.TaskResponseDto;
import com.example.text_corrector_api.service.api.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskCreatedResponse> create(@RequestBody @Valid TaskCreateDto taskCreateDto) {
        TaskCreatedResponse taskCreatedResponse = taskService.createTask(taskCreateDto);
        return new ResponseEntity<>(taskCreatedResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> get(@PathVariable UUID id) {
        TaskResponseDto taskResponseDto = taskService.getById(id);
        return new ResponseEntity<>(taskResponseDto, HttpStatus.OK);
    }


}
