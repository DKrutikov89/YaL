package com.yandex.lavka.exception;

import org.springframework.http.HttpStatus;

public class InvalidDateRangeException extends BusinessException {

    public InvalidDateRangeException(String messageKey, Object... args) {
        super(messageKey, ErrorCode.INVALID_DATE_RANGE, HttpStatus.UNPROCESSABLE_ENTITY, args);
    }
}
