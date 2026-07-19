package com.yandex.lavka.model.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class PeriodSummary {
    LocalDate startDate;
    LocalDate endDate;
    long days;
}
