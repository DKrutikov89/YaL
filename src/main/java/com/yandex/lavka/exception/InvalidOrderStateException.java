package com.yandex.lavka.exception;

public class InvalidOrderStateException extends RuntimeException {

    public InvalidOrderStateException(Long orderId, String message) {
        super("Order " + orderId + ": " + message);
    }
}
