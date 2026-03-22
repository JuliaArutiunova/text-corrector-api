package com.example.text_corrector_api.service.api;

import com.example.text_corrector_api.enums.Language;

public interface TextCorrectionService {
    String correct(String text, Language lang);
}
