package com.example.text_corrector_api.util;

import com.example.text_corrector_api.dto.SpellerErrorDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class TextRepairerTest {
    private final TextRepairer textRepairer = new TextRepairer();

    @ParameterizedTest(name = "{index} ==> {0}")
    @MethodSource("provideRepairScenarios")
    @DisplayName("Should correctly apply speller corrections to text")
    void shouldRepairTextCorrectly(String description, String original, List<SpellerErrorDto> errors, String expected){
        String result = textRepairer.applyCorrections(original, errors);

        assertThat(result)
                .as(description)
                .isEqualTo(expected);
    }

    private static Stream<Arguments> provideRepairScenarios() {
        return Stream.of(
                Arguments.of(
                        "Correct a single word",
                        "Hello wrld",
                        List.of(new SpellerErrorDto(1, 6, 4, "wrld", List.of("world"))),
                        "Hello world"
                ),
                Arguments.of(
                        "Correct multiple words",
                        "Java is amasing and beatiful",
                        List.of(
                                new SpellerErrorDto(1, 8, 7, "amasing", List.of("amazing")),
                                new SpellerErrorDto(1, 20, 8, "beatiful", List.of("beautiful"))
                        ),
                        "Java is amazing and beautiful"
                ),
                Arguments.of(
                        "Return original text if no errors",
                        "Perfect text",
                        Collections.emptyList(),
                        "Perfect text"
                )
        );
    }

}