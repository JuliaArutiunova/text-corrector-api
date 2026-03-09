package com.example.text_corrector_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SpellerErrorDto(
        int code,
        int pos,
        int len,
        String word,
        @JsonProperty("s")
        List<String> suggestions
) {
}
