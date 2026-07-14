package com.yandex.lavka.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CourierNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCourierNotFound(CourierNotFoundException ex) {
        logger.error("CourierNotFoundException: {}", ex.getMessage());

        Map<String, String> error = new HashMap<>();
        error.put("error", "Not Found");
        error.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleOrderNotFound(OrderNotFoundException ex) {
        logger.error("OrderNotFoundException: {}", ex.getMessage());

        Map<String, String> error = new HashMap<>();
        error.put("error", "Not Found");
        error.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(CourierOrderMismatchException.class)
    public ResponseEntity<Map<String, String>> handleCourierOrderMismatch(CourierOrderMismatchException ex) {
        logger.error("CourierOrderMismatchException: {}", ex.getMessage());

        Map<String, String> error = new HashMap<>();
        error.put("error", "Bad Request");
        error.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(InvalidOrderStateException.class)
    public ResponseEntity<Map<String, String>> handleInvalidOrderState(InvalidOrderStateException ex) {
        logger.error("InvalidOrderStateException: {}", ex.getMessage());

        Map<String, String> error = new HashMap<>();
        error.put("error", "Bad Request");
        error.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.error("Validation error: {}", ex.getMessage());

        Map<String, Object> errors = new HashMap<>();
        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = error instanceof FieldError fieldError
                    ? fieldError.getField()
                    : error.getObjectName();
            fieldErrors.put(fieldName, error.getDefaultMessage());
        });

        errors.put("error", "Validation Failed");
        errors.put("details", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler({ConstraintViolationException.class, HandlerMethodValidationException.class})
    public ResponseEntity<Map<String, Object>> handleConstraintViolations(Exception ex) {
        logger.error("Constraint violation: {}", ex.getMessage());

        Map<String, Object> error = new HashMap<>();
        error.put("error", "Validation Failed");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        logger.error("IllegalArgumentException: {}", ex.getMessage());

        Map<String, String> error = new HashMap<>();
        error.put("error", "Bad Request");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        logger.error("DataIntegrityViolationException: {}", ex.getMessage());

        Map<String, String> error = new HashMap<>();
        error.put("error", "Database Error");
        error.put("message", "The request violates database constraints");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneral(Exception ex) {
        logger.error("Unhandled exception: {}", ex.getMessage(), ex);

        Map<String, String> error = new HashMap<>();
        error.put("error", "Internal Server Error");
        error.put("message", "An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
