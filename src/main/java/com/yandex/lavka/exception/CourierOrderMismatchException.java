package com.yandex.lavka.exception;

public class CourierOrderMismatchException extends RuntimeException {

    public CourierOrderMismatchException(Long courierId, Long orderId) {
        super("Courier " + courierId + " is not assigned to order " + orderId);
    }
}
