package com.yandex.lavka.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OrderCompletionValidator.class)
public @interface ValidOrderCompletion {

    String message() default "{validation.order-completion.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
