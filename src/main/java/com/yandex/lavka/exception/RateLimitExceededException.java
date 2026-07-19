package com.yandex.lavka.exception;

import org.springframework.http.HttpStatus;

public class RateLimitExceededException extends BusinessException {

    public RateLimitExceededException(String key, int requestsPerSecond) {
        super(
                "error.rate-limit.exceeded",
                ErrorCode.RATE_LIMIT_EXCEEDED,
                HttpStatus.TOO_MANY_REQUESTS,
                key,
                requestsPerSecond
        );
    }
}
