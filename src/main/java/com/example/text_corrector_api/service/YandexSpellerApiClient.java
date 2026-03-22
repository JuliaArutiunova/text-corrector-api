package com.example.text_corrector_api.service;

import com.example.text_corrector_api.dto.SpellerErrorDto;
import com.example.text_corrector_api.enums.Language;
import com.example.text_corrector_api.exception.ExternalApiException;
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
public class YandexSpellerApiClient {
    private final RestClient restClient;
    private final String methodPath;

    public YandexSpellerApiClient(RestClient restClient, @Value("${speller.method-path}") String methodPath) {
        this.restClient = restClient;
        this.methodPath = methodPath;
    }

    /**
     * Sends a batch request to Yandex Speller API to check spelling for multiple text chunks.
     * <p>
     * All chunks are sent in a single request using application/x-www-form-urlencoded format.
     * The API returns a list of errors for each chunk in the same order.
     *
     * @param chunks  list of text chunks to check, must not be empty
     * @param lang    language of the text
     * @param options bitmask of API options
     * @return list of spelling errors for each chunk, in the same order as input chunks
     * @throws ExternalApiException if the API call fails or returns an HTTP error
     */
    public List<List<SpellerErrorDto>> checkTexts(List<String> chunks, Language lang, int options) {
        log.debug("Sending {} chunks to Yandex Speller. Options: {}, Lang: {}", chunks.size(), options, lang);

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