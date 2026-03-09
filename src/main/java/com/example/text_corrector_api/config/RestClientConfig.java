package com.example.text_corrector_api.config;

import com.example.text_corrector_api.exception.ExternalApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Bean(name = "yandexClient")
    public RestClient yandexSpellerClient(RestClient.Builder restClientBuilder,
                                          @Value("${speller.url}") String baseUrl,
                                          @Value("${speller.connect-timeout}") Duration connectTimeout,
                                          @Value("${speller.read-timeout}") Duration readTimeout) {
        return restClientBuilder
                .baseUrl(baseUrl)
                .requestFactory(ClientHttpRequestFactoryBuilder.detect()
                        .build(ClientHttpRequestFactorySettings.defaults()
                                .withConnectTimeout(connectTimeout)
                                .withReadTimeout(readTimeout)))
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                    throw new ExternalApiException(
                            "Yandex Speller error: " + response.getStatusText(),
                            response.getStatusCode().value());
                })
                .build();
    }
}
