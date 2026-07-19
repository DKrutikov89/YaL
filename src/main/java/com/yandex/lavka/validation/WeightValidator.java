package com.yandex.lavka.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class WeightValidator implements ConstraintValidator<ValidWeight, BigDecimal> {

    private BigDecimal min;
    private BigDecimal max;

    @Override
    public void initialize(ValidWeight constraintAnnotation) {
        this.min = new BigDecimal(constraintAnnotation.min());
        this.max = new BigDecimal(constraintAnnotation.max());
    }

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
    }
}
