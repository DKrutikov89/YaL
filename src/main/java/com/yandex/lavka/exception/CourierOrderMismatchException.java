package com.yandex.lavka.exception;

import org.springframework.http.HttpStatus;

public class CourierOrderMismatchException extends BusinessException {

    public CourierOrderMismatchException(Long courierId, Long orderId) {
        super(
                "error.order.courier-mismatch",
                ErrorCode.COURIER_ORDER_MISMATCH,
                HttpStatus.CONFLICT,
                courierId,
                orderId
        );
    }
}
