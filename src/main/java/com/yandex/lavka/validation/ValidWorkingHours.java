package com.yandex.lavka.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Кастомная аннотация для проверки рабочего времени.
 * Проверяет, что время начала меньше времени окончания.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = WorkingHoursValidator.class)
@Documented
public @interface ValidWorkingHours {

    String message() default "Working hours must be in format 'HH:MM-HH:MM' and start time must be before end time";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}