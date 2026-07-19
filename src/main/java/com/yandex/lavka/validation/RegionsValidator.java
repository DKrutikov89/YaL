package com.yandex.lavka.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RegionsValidator implements ConstraintValidator<ValidRegions, List<Integer>> {

    private int max;

    @Override
    public void initialize(ValidRegions constraintAnnotation) {
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(List<Integer> regions, ConstraintValidatorContext context) {
        if (regions == null || regions.isEmpty()) {
            return true;
        }

        if (regions.size() > max) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{validation.regions.too-many}")
                    .addConstraintViolation();
            return false;
        }

        Set<Integer> uniqueRegions = new HashSet<>();
        for (Integer region : regions) {
            if (region == null || region <= 0) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("{validation.regions.positive}")
                        .addConstraintViolation();
                return false;
            }
            if (!uniqueRegions.add(region)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("{validation.regions.duplicates}")
                        .addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}
