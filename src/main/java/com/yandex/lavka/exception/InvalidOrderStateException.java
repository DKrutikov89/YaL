package com.yandex.lavka.exception;

import org.springframework.http.HttpStatus;

public class InvalidOrderStateException extends BusinessException {

    public InvalidOrderStateException(Long orderId, String reasonKey) {
        super(
                reasonKey,
                ErrorCode.ORDER_ALREADY_COMPLETED,
                HttpStatus.CONFLICT,
                orderId
        );
    }
}
