package com.example.text_corrector_api.exception;

import com.example.text_corrector_api.enums.ErrorCode;
import lombok.Getter;


@Getter
public class ExternalApiException extends RuntimeException{
    private final int httpStatus;
    private final ErrorCode errorCode = ErrorCode.EXTERNAL_API_ERROR;

    public ExternalApiException(String message, int httpStatusCode) {
        super(message);
        this.httpStatus = httpStatusCode;
    }
}
