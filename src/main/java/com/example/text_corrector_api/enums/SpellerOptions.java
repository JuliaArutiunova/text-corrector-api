package com.example.text_corrector_api.enums;

import lombok.Getter;

@Getter
public enum SpellerOptions {
    IGNORE_DIGITS(2),
    IGNORE_URLS(4),
    FIND_REPEAT_WORDS(8),
    IGNORE_CAPITALIZATION(512);

    private final int code;

    SpellerOptions(int code) {
        this.code = code;
    }
}
