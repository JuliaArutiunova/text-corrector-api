package com.example.text_corrector_api.util;

import com.example.text_corrector_api.enums.SpellerOptions;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class YandexOptionsCalculator {

    private static final Pattern DIGIT_PATTERN = Pattern.compile("\\d");

    private static final Pattern URL_PATTERN = Pattern.compile("(https?|ftp|www)\\S+",
            Pattern.CASE_INSENSITIVE);


    public int calculateOptions(String text) {
        int options = 0;
        if (DIGIT_PATTERN.matcher(text).find()) {
            options |= SpellerOptions.IGNORE_DIGITS.getCode();
        }
        if (URL_PATTERN.matcher(text).find()) {
            options |= SpellerOptions.IGNORE_URLS.getCode();
        }
        return options;
    }

}
