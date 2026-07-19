package com.yandex.lavka.exception;

import org.springframework.http.HttpStatus;

public class InvalidTimeFormatException extends BusinessException {

    public InvalidTimeFormatException(String value) {
        super(
                "error.time.invalid-format",
                ErrorCode.INVALID_TIME_FORMAT,
                HttpStatus.UNPROCESSABLE_ENTITY,
                value
        );
    }
}
