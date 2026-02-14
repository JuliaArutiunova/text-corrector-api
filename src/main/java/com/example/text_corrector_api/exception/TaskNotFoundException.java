package com.example.text_corrector_api.exception;

import com.example.text_corrector_api.enums.ErrorCode;
import lombok.Getter;

import java.util.UUID;

@Getter
public class TaskNotFoundException extends RuntimeException {
    private final UUID taskId;

    public TaskNotFoundException(UUID taskId) {
        super(String.format("Task with id: %s not found", taskId));
        this.taskId = taskId;
    }

    public int getErrorCode() {
        return ErrorCode.TASK_NOT_FOUND.getCode();
    }
}
