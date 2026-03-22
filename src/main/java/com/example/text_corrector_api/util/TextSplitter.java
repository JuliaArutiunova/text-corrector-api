package com.example.text_corrector_api.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TextSplitter {
    private static final Pattern BOUNDARY_PATTERN = Pattern.compile("[.,!?;:\\n]");

    /**
     * Splits text into chunks not exceeding the specified limit, preserving natural boundaries.
     * <p>
     * Strategy:
     * - Tries to split at sentence boundaries: ., !, ?, ;, :, \n
     * - If no boundary found, falls back to the last space within the limit
     * - Otherwise, cuts at the exact limit
     *
     * @param text  the text to split
     * @param limit maximum chunk length in characters
     * @return a list of text chunks, or an empty list if input is null
     */
    public List<String> split(String text, int limit) {
        if (text == null || text.length() <= limit) {
            return Collections.singletonList(text);
        }

        List<String> chunks = new ArrayList<>();
        int start = 0;

        while (start < text.length()) {
            int end = Math.min(start + limit, text.length());

            if (end < text.length()) {
                int boundaryIndex = findLastBoundary(text, start, end);

                if (boundaryIndex != -1) {
                    end = boundaryIndex + 1;
                } else {
                    int lastSpace = text.lastIndexOf(' ', end);
                    if (lastSpace > start) {
                        end = lastSpace + 1;
                    }
                }
            }

            chunks.add(text.substring(start, end));
            start = end;
        }
        return chunks;

    }

    private int findLastBoundary(String text, int start, int end) {
        Matcher matcher = BOUNDARY_PATTERN.matcher(text);
        matcher.region(start, end);

        int lastMatch = -1;
        while (matcher.find()) {
            lastMatch = matcher.start();
        }
        return lastMatch;
    }
}
