package com.yandex.lavka.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RegionsValidator.class)
public @interface ValidRegions {

    String message() default "{validation.regions.invalid}";

    int max() default 50;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
