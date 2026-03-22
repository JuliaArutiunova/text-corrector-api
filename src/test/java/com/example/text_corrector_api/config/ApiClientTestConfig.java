package com.example.text_corrector_api.config;

import com.example.text_corrector_api.util.TextRepairer;
import com.example.text_corrector_api.util.TextSplitter;
import com.example.text_corrector_api.util.YandexOptionsCalculator;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;

@TestConfiguration
public class ApiClientTestConfig {

    @Bean
    @Primary
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public TextSplitter textSplitter() {
        return new TextSplitter();
    }

    @Bean
    public YandexOptionsCalculator optionsCalculator() {
        return new YandexOptionsCalculator();
    }

    @Bean
    public TextRepairer textRepairer() {
        return new TextRepairer();
    }

}
