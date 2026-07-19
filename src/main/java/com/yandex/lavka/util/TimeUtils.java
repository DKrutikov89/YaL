package com.yandex.lavka.util;

import com.yandex.lavka.exception.InvalidDateRangeException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class TimeUtils {

    public void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new InvalidDateRangeException("error.date-range.required");
        }
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException("error.date-range.invalid-order", startDate, endDate);
        }
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        if (days > 366) {
            throw new InvalidDateRangeException("error.date-range.too-large", days);
        }
    }

    public long calculateDaysInclusive(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    public BigDecimal calculateHoursBetween(LocalTime start, LocalTime end) {
        long minutes = ChronoUnit.MINUTES.between(start, end);
        if (minutes < 0) {
            minutes += 24 * 60;
        }
        return BigDecimal.valueOf(minutes)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal getDailyWorkingHours(List<String> workingHours) {
        if (workingHours == null || workingHours.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return workingHours.stream()
                .map(this::parseWorkingIntervalHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalWorkingHours(List<String> workingHours, LocalDate startDate, LocalDate endDate) {
        BigDecimal dailyWorkingHours = getDailyWorkingHours(workingHours);
        long days = calculateDaysInclusive(startDate, endDate);
        return dailyWorkingHours.multiply(BigDecimal.valueOf(days))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal parseWorkingIntervalHours(String interval) {
        String[] parts = interval.split("-");
        LocalTime start = LocalTime.parse(parts[0]);
        LocalTime end = LocalTime.parse(parts[1]);
        return calculateHoursBetween(start, end);
    }
}
