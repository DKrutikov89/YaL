package com.yandex.lavka.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Валидатор для аннотации @ValidWorkingHours.
 * Проверяет, что каждый интервал времени корректен.
 */
public class WorkingHoursValidator implements ConstraintValidator<ValidWorkingHours, List<String>> {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public void initialize(ValidWorkingHours constraintAnnotation) {
        // Инициализация не требуется
    }

    @Override
    public boolean isValid(List<String> workingHours, ConstraintValidatorContext context) {
        // Если список null или пустой — пропускаем (другие аннотации проверят это)
        if (workingHours == null || workingHours.isEmpty()) {
            return true;
        }

        // Проверяем каждый интервал
        for (String interval : workingHours) {
            if (!isValidInterval(interval)) {
                // Добавляем детальное сообщение об ошибке
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "Invalid interval: '" + interval + "' - start time must be before end time"
                ).addConstraintViolation();
                return false;
            }
        }

        return true;
    }

    /**
     * Проверяет один интервал времени
     */
    private boolean isValidInterval(String interval) {
        if (interval == null || interval.isEmpty()) {
            return false;
        }

        // Разделяем интервал на начало и конец
        String[] parts = interval.split("-");

        // Должно быть ровно 2 части
        if (parts.length != 2) {
            return false;
        }

        try {
            // Парсим время начала и окончания
            LocalTime start = LocalTime.parse(parts[0].trim(), TIME_FORMATTER);
            LocalTime end = LocalTime.parse(parts[1].trim(), TIME_FORMATTER);

            // Проверяем: начало должно быть раньше окончания
            return start.isBefore(end);

        } catch (Exception e) {
            // Если парсинг не удался — интервал невалидный
            return false;
        }
    }
}