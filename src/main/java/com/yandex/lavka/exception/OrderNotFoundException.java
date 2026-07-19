package com.yandex.lavka.exception;

import org.springframework.http.HttpStatus;

public class OrderNotFoundException extends BusinessException {

    public OrderNotFoundException(Long orderId) {
        super(
                "error.order.not-found",
                ErrorCode.ORDER_NOT_FOUND,
                HttpStatus.NOT_FOUND,
                orderId
        );
    }
}
