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

    String message() default "{validation.working-hours.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
