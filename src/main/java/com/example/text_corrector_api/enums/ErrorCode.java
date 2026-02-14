package com.example.text_corrector_api.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public enum ErrorCode {
    TASK_NOT_FOUND(40401, HttpStatus.NOT_FOUND, "Task with id: %s not found"),
    VALIDATION_ERROR(40001, HttpStatus.BAD_REQUEST, "Validation failed"),
    EXTERNAL_API_ERROR(50201, HttpStatus.BAD_GATEWAY, "External API error");

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(int code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message= message;
    }
}
