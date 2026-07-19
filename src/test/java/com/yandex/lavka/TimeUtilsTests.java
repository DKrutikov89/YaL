package com.yandex.lavka;

import com.yandex.lavka.exception.InvalidDateRangeException;
import com.yandex.lavka.util.TimeUtils;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TimeUtilsTests {

    private final TimeUtils timeUtils = new TimeUtils();

    @Test
    void calculateHoursBetweenReturnsExpectedDuration() {
        assertThat(timeUtils.calculateHoursBetween(LocalTime.of(9, 0), LocalTime.of(18, 0)))
                .isEqualByComparingTo(new BigDecimal("9.00"));
    }

    @Test
    void calculateHoursBetweenHandlesMidnightCrossing() {
        assertThat(timeUtils.calculateHoursBetween(LocalTime.of(22, 0), LocalTime.of(2, 0)))
                .isEqualByComparingTo(new BigDecimal("4.00"));
    }

    @Test
    void getTotalWorkingHoursUsesInclusiveDays() {
        assertThat(timeUtils.getTotalWorkingHours(
                List.of("09:00-18:00"),
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 1, 31)))
                .isEqualByComparingTo(new BigDecimal("279.00"));
    }

    @Test
    void validateDateRangeRejectsReversePeriod() {
        assertThatThrownBy(() -> timeUtils.validateDateRange(
                LocalDate.of(2023, 1, 31),
                LocalDate.of(2023, 1, 1)))
                .isInstanceOf(InvalidDateRangeException.class);
    }
}
