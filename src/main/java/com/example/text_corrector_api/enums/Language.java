package com.example.text_corrector_api.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum Language {
    RU, EN;

    private static final Map<String, Language> LOOKUP = Arrays.stream(values())
            .collect(Collectors.toMap(
                    language -> language.name().toLowerCase(),
                    language -> language));

    @JsonCreator
    public static Language fromString(String value) {
        if (value == null) {
            return null;
        }
        return LOOKUP.get(value.toLowerCase());
    }


}
