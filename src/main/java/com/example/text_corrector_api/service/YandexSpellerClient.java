package com.example.text_corrector_api.service;

import com.example.text_corrector_api.dto.SpellerErrorDto;
import com.example.text_corrector_api.enums.Language;
import com.example.text_corrector_api.exception.ExternalApiException;
import com.example.text_corrector_api.service.api.TextCorrectionClient;
import com.example.text_corrector_api.util.TextRepairer;
import com.example.text_corrector_api.util.TextSplitter;
import com.example.text_corrector_api.util.YandexOptionsCalculator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@Slf4j
public class YandexSpellerClient implements TextCorrectionClient {

    private final RestClient restClient;
    private final TextSplitter textSplitter;
    private final YandexOptionsCalculator optionsCalculator;
    private final TextRepairer textRepairer;
    @Value("${speller.text-limit}")
    private int textLimit;
    @Value("${speller.method-path}")
    private String methodPath;


    public YandexSpellerClient(RestClient restClient, TextSplitter textSplitter,
                               YandexOptionsCalculator optionsCalculator, TextRepairer textRepairer) {
        this.restClient = restClient;
        this.textSplitter = textSplitter;
        this.optionsCalculator = optionsCalculator;
        this.textRepairer = textRepairer;
    }

    @Override
    public String correct(String text, Language lang) {
        int options = optionsCalculator.calculateOptions(text);
        List<String> chunks = textSplitter.split(text, textLimit);

        List<List<SpellerErrorDto>> allErrors = callYandexApi(chunks, lang, options);

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

    private List<List<SpellerErrorDto>> callYandexApi(List<String> chunks, Language lang, int options) {
        log.debug("Sending {} chunks to Yandex Speller. Options: {}, Lang: {}", chunks.size(), options, lang);

        try {
            return restClient
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .path(methodPath)
                            .queryParam(SpellerParams.LANG, lang.name().toLowerCase())
                            .queryParam(SpellerParams.OPTIONS, options)
                            .build())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(createFormBody(chunks))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (ExternalApiException e) {
            log.error("Failed to call Yandex Speller API: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while calling Yandex API", e);
            throw new ExternalApiException("Unexpected error: " + e.getMessage(), 500);
        }
    }

    private MultiValueMap<String, String> createFormBody(List<String> chunks) {
        MultiValueMap<String, String> formParams = new LinkedMultiValueMap<>();
        chunks.forEach(chunk -> formParams.add(SpellerParams.TEXT, chunk));
        return formParams;
    }

    private static class SpellerParams {
        static final String LANG = "lang";
        static final String OPTIONS = "options";
        static final String TEXT = "text";
    }
}
