package com.yandex.lavka.validation;

import com.yandex.lavka.model.dto.CompleteOrderDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class OrderCompletionValidator implements ConstraintValidator<ValidOrderCompletion, CompleteOrderDto> {

    @Override
    public boolean isValid(CompleteOrderDto value, ConstraintValidatorContext context) {
        if (value == null || value.getCompleteTime() == null) {
            return true;
        }

        return !value.getCompleteTime().isAfter(LocalDateTime.now());
    }
}
