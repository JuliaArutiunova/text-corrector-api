package com.example.text_corrector_api.dto;

import com.example.text_corrector_api.enums.Language;

import java.util.UUID;

public record TaskProcessingContext(
        UUID id,
        String text,
        Language language) {
}
