package com.example.text_corrector_api.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class TextSplitterTest {
    private final TextSplitter textSplitter = new TextSplitter();

    @ParameterizedTest(name = "{index} ==> {0}")
    @MethodSource("provideStringsForSplitting")
    @DisplayName("Should correctly split text into chunks based on limit")
    void shouldCorrectlySplitText(String description, String text, int limit, int expectedSize) {
        List<String> result = textSplitter.split(text, limit);

        assertThat(result)
                .as(description)
                .hasSize(expectedSize);
        assertThat(String.join("", result)).isEqualTo(text);

        result.forEach(chunk ->
                assertThat(chunk.length()).isLessThanOrEqualTo(limit)
        );
    }

    private static Stream<Arguments> provideStringsForSplitting() {
        return Stream.of(
                Arguments.of("Short text stays as one chunk", "Hello world", 100, 1),
                Arguments.of("Split exactly at limit", "1234567890", 5, 2),
                Arguments.of("Split at space if limit is in middle of word", "Hello beautiful world", 10, 3),
                Arguments.of("Text with multiple sentences", "First sentence. Second phrase. Third.", 15, 3),
                Arguments.of("Empty text returns empty list", "", 10, 1));
    }

    @Test
    @DisplayName("Should handle text with no spaces by forcing split at limit")
    void shouldHandleNoSpacesText() {
        String longWord = "VeryLongWordWithoutAnySpacesAtAll";
        int limit = 10;

        List<String> result = textSplitter.split(longWord, limit);

        assertThat(result).hasSizeGreaterThan(1);
        assertThat(String.join("", result)).isEqualTo(longWord);
    }


}