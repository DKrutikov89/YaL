package com.yandex.lavka.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = WeightValidator.class)
public @interface ValidWeight {

    String message() default "{validation.weight.invalid}";

    String min() default "0.1";

    String max() default "50.0";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
