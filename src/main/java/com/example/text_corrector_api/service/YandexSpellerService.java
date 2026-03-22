package com.example.text_corrector_api.service;

import com.example.text_corrector_api.dto.SpellerErrorDto;
import com.example.text_corrector_api.enums.Language;
import com.example.text_corrector_api.exception.ExternalApiException;
import com.example.text_corrector_api.service.api.TextCorrectionService;
import com.example.text_corrector_api.util.TextRepairer;
import com.example.text_corrector_api.util.TextSplitter;
import com.example.text_corrector_api.util.YandexOptionsCalculator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class YandexSpellerService implements TextCorrectionService {
    private final TextSplitter textSplitter;
    private final TextRepairer textRepairer;
    private final YandexOptionsCalculator optionsCalculator;
    private final YandexSpellerApiClient yandexSpellerApiClient;
    private final int textLimit;


    public YandexSpellerService(TextSplitter textSplitter, YandexOptionsCalculator optionsCalculator,
                                TextRepairer textRepairer, YandexSpellerApiClient yandexSpellerApiClient,
                                @Value("${speller.text-limit}") int textLimit) {
        this.textSplitter = textSplitter;
        this.optionsCalculator = optionsCalculator;
        this.textRepairer = textRepairer;
        this.yandexSpellerApiClient = yandexSpellerApiClient;
        this.textLimit = textLimit;
    }
    /**
     * Corrects spelling errors in the given text using Yandex Speller API.
     *
     * @param text the text to correct
     * @param lang the language of the text
     * @return corrected text with spelling errors fixed
     * @throws ExternalApiException if the API call fails or returns an unexpected response
     */
    @Override
    public String correct(String text, Language lang) {
        int options = optionsCalculator.calculateOptions(text);
        List<String> chunks = textSplitter.split(text, textLimit);

        List<List<SpellerErrorDto>> allErrors = yandexSpellerApiClient.checkTexts(chunks, lang, options);

        if (chunks.size() != allErrors.size()) {
            log.error("Yandex API returned unexpected number of results. Expected: {}, Got: {}",
                    chunks.size(), allErrors.size());
            throw new ExternalApiException("Response size mismatch", 500);
        }

        StringBuilder finalResult = new StringBuilder();
        for (int i = 0; i < chunks.size(); i++) {
            String correctedChunk = textRepairer.applyCorrections(chunks.get(i), allErrors.get(i));
            finalResult.append(correctedChunk);
        }

        return finalResult.toString();
    }

}
