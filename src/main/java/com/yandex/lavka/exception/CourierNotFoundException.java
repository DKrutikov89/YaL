package com.yandex.lavka.exception;

import org.springframework.http.HttpStatus;

public class CourierNotFoundException extends BusinessException {

    public CourierNotFoundException(Long courierId) {
        super(
                "error.courier.not-found",
                ErrorCode.COURIER_NOT_FOUND,
                HttpStatus.NOT_FOUND,
                courierId
        );
    }
}
