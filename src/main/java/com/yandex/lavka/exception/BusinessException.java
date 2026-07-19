package com.yandex.lavka.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final HttpStatus httpStatus;
    private final String messageKey;
    private final Object[] messageArgs;

    public BusinessException(
            String messageKey,
            ErrorCode errorCode,
            HttpStatus httpStatus,
            Object... messageArgs) {
        super(messageKey);
        this.messageKey = messageKey;
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.messageArgs = messageArgs;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public Object[] getMessageArgs() {
        return messageArgs;
    }
}
