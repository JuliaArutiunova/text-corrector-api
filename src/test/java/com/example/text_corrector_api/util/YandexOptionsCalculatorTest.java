package com.example.text_corrector_api.util;

import com.example.text_corrector_api.enums.SpellerOptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


class YandexOptionsCalculatorTest {
    private final YandexOptionsCalculator yandexOptionsCalculator = new YandexOptionsCalculator();

    @ParameterizedTest(name = "{index} ==> {0}")
    @MethodSource("provideTextForOptionsCalculation")
    @DisplayName("Should calculate options correctly")
    void shouldCalculateOptionsCorrectly(String description, String text, int expected) {
        int result = yandexOptionsCalculator.calculateOptions(text);
        assertThat(result).isEqualTo(expected);
    }


    private static Stream<Arguments> provideTextForOptionsCalculation() {
        return Stream.of(
                Arguments.of("Should ignore digits when text contains numbers", "There are 2 windows",
                        SpellerOptions.IGNORE_DIGITS.getCode()),
                Arguments.of("Should ignore URLs when text contains links", "Check this https://google.com",
                        SpellerOptions.IGNORE_URLS.getCode()),
                Arguments.of("Should combine flags when both digits and links are present",
                        "Digit 25 and url www.youtube.com",
                        SpellerOptions.IGNORE_DIGITS.getCode() + SpellerOptions.IGNORE_URLS.getCode()),
                Arguments.of("Should return zero for plain text", "Hello world", 0)
        );
    }

}