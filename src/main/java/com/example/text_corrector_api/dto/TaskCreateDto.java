package com.example.text_corrector_api.dto;

import com.example.text_corrector_api.enums.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public record TaskCreateDto(
        @NotBlank(message = "Text required")
        @Size(min = 3, message = "Text must be at least 3 characters long")
        @Pattern(
                regexp = ".*\\p{L}.*",
                message = "Text must contain at least one letter"
        )
        String text,
        @NotNull(message = "Language not supported or missing")
        Language language
) {
}
