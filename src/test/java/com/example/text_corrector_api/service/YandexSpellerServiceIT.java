package com.example.text_corrector_api.service;

import com.example.text_corrector_api.config.ApiClientTestConfig;
import com.example.text_corrector_api.config.RestClientConfig;
import com.example.text_corrector_api.dto.SpellerErrorDto;
import com.example.text_corrector_api.enums.Language;
import com.example.text_corrector_api.exception.ExternalApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = {
        YandexSpellerService.class,
        YandexSpellerApiClient.class,
        RestClientConfig.class,
        ApiClientTestConfig.class
})
@ActiveProfiles("test")
public class YandexSpellerServiceIT {
    @Autowired
    private YandexSpellerService yandexSpellerService;

    @Value("${speller.method-path}")
    private String spellerMethodPath;

    private static WireMockServer wireMockServer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(0);
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    @AfterAll
    static void stopWireMock() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @BeforeEach
    void setUp() {
        wireMockServer.resetAll();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("speller.url",
                () -> "http://localhost:" + wireMockServer.port());
    }

    @Test
    @DisplayName("Should successfully adjust the text with one chunk")
    void shouldCorrectSingleChunk() throws Exception {
        String originalText = "Превет, как делы?";
        Language language = Language.RU;

        List<SpellerErrorDto> errors = List.of(
                new SpellerErrorDto(1, 0, 6, "Превет", List.of("Привет")),
                new SpellerErrorDto(1, 12, 4, "делы", List.of("дела"))
        );
        List<List<SpellerErrorDto>> apiResponse = List.of(errors);

        stubFor(post(urlPathEqualTo(spellerMethodPath))
                .withQueryParam("lang", equalTo("ru"))
                .withQueryParam("options", equalTo("0"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody(objectMapper.writeValueAsString(apiResponse))));

        String correctedText = yandexSpellerService.correct(originalText, language);

        assertThat(correctedText).isEqualTo("Привет, как дела?");
    }

    @Test
    @DisplayName("Should throw ExternalApiException when API returns error")
    void shouldThrowExceptionWhenApiReturnsError() throws Exception {
        String originalText = "Превет, как делы?";
        Language language = Language.RU;

        stubFor(post(urlPathEqualTo(spellerMethodPath))
                .withQueryParam("lang", equalTo("ru"))
                .withQueryParam("options", equalTo("0"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("Internal Server Error")));

        assertThatThrownBy(() -> yandexSpellerService.correct(originalText, language))
                .isInstanceOf(ExternalApiException.class)
                .hasMessageContaining("Yandex Speller error");
    }

}
