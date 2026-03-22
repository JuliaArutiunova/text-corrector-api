package com.example.text_corrector_api.service;

import com.example.text_corrector_api.dto.SpellerErrorDto;
import com.example.text_corrector_api.enums.Language;
import com.example.text_corrector_api.exception.ExternalApiException;
import com.example.text_corrector_api.util.TextRepairer;
import com.example.text_corrector_api.util.TextSplitter;
import com.example.text_corrector_api.util.YandexOptionsCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YandexSpellerServiceTest {
    @Mock
    private TextSplitter textSplitter;
    @Mock
    private YandexOptionsCalculator optionsCalculator;
    @Mock
    private TextRepairer textRepairer;
    @Mock
    private YandexSpellerApiClient yandexSpellerApiClient;
    private final int limit = 1000;
    private YandexSpellerService yandexSpellerService;


    @BeforeEach
    void setUp() {
        yandexSpellerService = new YandexSpellerService(
                textSplitter, optionsCalculator, textRepairer, yandexSpellerApiClient, limit
        );
    }

    @Test
    @DisplayName("Should successfully orchestrate correction for multiple chunks")
    void shouldCorrectMultipleChunks() {
        String text = "text1 text2";
        List<String> chunks = List.of("text1", "text2");
        List<List<SpellerErrorDto>> mockApiResult = List.of(new ArrayList<>(), new ArrayList<>());

        when(optionsCalculator.calculateOptions(text)).thenReturn(2);
        when(textSplitter.split(text, limit)).thenReturn(chunks);
        when(yandexSpellerApiClient.checkTexts(chunks, Language.EN, 2)).thenReturn(mockApiResult);
        when(textRepairer.applyCorrections(eq("text1"), any())).thenReturn("Text1");
        when(textRepairer.applyCorrections(eq("text2"), any())).thenReturn("Text2");

        String result = yandexSpellerService.correct(text, Language.EN);

        assertThat(result).isEqualTo("Text1Text2");

        verify(textSplitter).split(text, limit);
        verify(optionsCalculator).calculateOptions(text);
        verify(yandexSpellerApiClient).checkTexts(chunks, Language.EN, 2);
        verify(textRepairer).applyCorrections(eq("text1"), any());
        verify(textRepairer).applyCorrections(eq("text2"), any());
    }

    @Test
    @DisplayName("Should throw ExternalApiException when API response size mismatch")
    void shouldThrowExceptionOnSizeMismatch() {
        List<String> chunks = List.of("chunk1", "chunk2");
        List<List<SpellerErrorDto>> badApiResult = List.of(List.of());

        when(textSplitter.split(anyString(), anyInt())).thenReturn(chunks);
        when(yandexSpellerApiClient.checkTexts(anyList(), any(), anyInt())).thenReturn(badApiResult);

        assertThatThrownBy(() -> yandexSpellerService.correct("any", Language.EN))
                .isInstanceOf(ExternalApiException.class)
                .hasMessageContaining("Response size mismatch");
    }

    @Test
    @DisplayName("Should propagate exception from API client")
    void shouldPropagateApiClientException() {
        String text = "test text";
        List<String> chunks = List.of("test text");

        when(textSplitter.split(text, limit)).thenReturn(chunks);
        when(yandexSpellerApiClient.checkTexts(anyList(), any(), anyInt()))
                .thenThrow(new ExternalApiException("API error", 500));

        assertThatThrownBy(() -> yandexSpellerService.correct(text, Language.EN))
                .isInstanceOf(ExternalApiException.class)
                .hasMessageContaining("API error");
    }
}