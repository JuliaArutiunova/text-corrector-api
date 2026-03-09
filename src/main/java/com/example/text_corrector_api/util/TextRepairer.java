package com.example.text_corrector_api.util;

import com.example.text_corrector_api.dto.SpellerErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@Slf4j
public class TextRepairer {

    public String applyCorrections(String originalText, List<SpellerErrorDto> errors) {
        if (originalText == null || originalText.isEmpty() || errors == null || errors.isEmpty()) {
            return originalText;
        }

        StringBuilder sb = new StringBuilder(originalText);

        errors.stream()
                .sorted(Comparator.comparingInt(SpellerErrorDto::pos).reversed())
                .forEach(error -> applySingleCorrection(sb, error));

        return sb.toString();
    }

    private void applySingleCorrection(StringBuilder sb, SpellerErrorDto error) {
        if (error.suggestions() == null || error.suggestions().isEmpty()) {
            return;
        }

        String replacement = error.suggestions().getFirst();
        int start = error.pos();
        int end = start + error.len();

        if (start >= 0 && end <= sb.length()) {
            sb.replace(start, end, replacement);
        } else {
            log.warn("Speller sent out-of-bounds coordinates: pos={}, len={} for text length {}",
                    start, error.len(), sb.length());
        }
    }
}
