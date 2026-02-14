package com.example.text_corrector_api.dto;

import com.example.text_corrector_api.enums.TaskStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaskResponseDto(
        UUID id,
        TaskStatus status,
        String correctedText,
        String errorMessage
) {
}
