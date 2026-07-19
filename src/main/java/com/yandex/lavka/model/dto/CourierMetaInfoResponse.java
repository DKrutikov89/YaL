package com.yandex.lavka.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yandex.lavka.model.enums.CourierType;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourierMetaInfoResponse {
    Long courierId;
    CourierType courierType;
    List<Integer> regions;
    List<String> workingHours;
    BigDecimal rating;
    Long earnings;
}
