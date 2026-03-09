package com.example.text_corrector_api.service.api;

import com.example.text_corrector_api.enums.Language;

public interface TextCorrectionClient {
    String correct(String text, Language lang);
}
