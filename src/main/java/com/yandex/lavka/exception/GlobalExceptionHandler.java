package com.yandex.lavka.exception;

import com.yandex.lavka.config.CorrelationIdFilter;
import com.yandex.lavka.model.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        logger.warn("Business exception [{}]: {}", ex.getErrorCode(), ex.getMessageKey());
        return buildResponse(
                ex.getHttpStatus(),
                resolveMessage(ex.getMessageKey(), ex.getMessageArgs()),
                request,
                ex.getErrorCode(),
                null
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        logger.warn("Validation error: {}", ex.getMessage());

        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = error instanceof FieldError fieldError
                    ? fieldError.getField()
                    : error.getObjectName();
            fieldErrors.put(fieldName, resolveMessage(error.getDefaultMessage(), null));
        });

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                resolveMessage("error.validation.failed", null),
                request,
                ErrorCode.VALIDATION_ERROR,
                fieldErrors
        );
    }

    @ExceptionHandler({ConstraintViolationException.class, HandlerMethodValidationException.class})
    public ResponseEntity<ErrorResponse> handleConstraintViolations(Exception ex, HttpServletRequest request) {
        logger.warn("Constraint violation: {}", ex.getMessage());
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                resolveMessage("error.validation.failed", null),
                request,
                ErrorCode.VALIDATION_ERROR,
                Map.of("request", ex.getMessage())
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        logger.warn("IllegalArgumentException: {}", ex.getMessage());
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request,
                ErrorCode.VALIDATION_ERROR,
                null
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {
        logger.error("DataIntegrityViolationException: {}", ex.getMessage());
        return buildResponse(
                HttpStatus.CONFLICT,
                resolveMessage("error.database.constraint", null),
                request,
                ErrorCode.DATABASE_CONSTRAINT_VIOLATION,
                null
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableMessage(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        logger.warn("Unreadable JSON payload: {}", ex.getMessage());
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                resolveMessage("error.json.invalid", null),
                request,
                ErrorCode.INVALID_JSON,
                null
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {
        logger.warn("Invalid request parameter type: {}", ex.getMessage());
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                resolveMessage("error.date-format.invalid", null),
                request,
                ErrorCode.INVALID_DATE_FORMAT,
                Map.of(ex.getName(), ex.getValue() == null ? "null" : ex.getValue().toString())
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex, HttpServletRequest request) {
        logger.error("Unhandled exception: {}", ex.getMessage(), ex);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                resolveMessage("error.internal", null),
                request,
                ErrorCode.INTERNAL_ERROR,
                null
        );
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            ErrorCode errorCode,
            Map<String, String> details) {
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .details(details == null || details.isEmpty() ? null : details)
                .errorCode(errorCode)
                .correlationId((String) request.getAttribute(CorrelationIdFilter.ATTRIBUTE_NAME))
                .build();
        return ResponseEntity.status(status).body(body);
    }

    private String resolveMessage(String key, Object[] args) {
        Locale locale = LocaleContextHolder.getLocale();
        if (key == null) {
            return null;
        }
        if (!key.startsWith("{") || !key.endsWith("}")) {
            return messageSource.getMessage(key, args, key, locale);
        }
        String trimmedKey = key.substring(1, key.length() - 1);
        return messageSource.getMessage(trimmedKey, args, trimmedKey, locale);
    }
}
