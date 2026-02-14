package com.example.text_corrector_api.error_handler;

import com.example.text_corrector_api.dto.ErrorResponse;
import com.example.text_corrector_api.enums.ErrorCode;
import com.example.text_corrector_api.exception.TaskNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
                                                                      HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                e.getFieldError().getDefaultMessage(),
                ErrorCode.VALIDATION_ERROR.getCode(),
                OffsetDateTime.now(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskNotFoundException(TaskNotFoundException e,
                                                                     HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                ErrorCode.TASK_NOT_FOUND.getCode(),
                OffsetDateTime.now(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
