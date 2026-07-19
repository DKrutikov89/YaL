package com.yandex.lavka.model.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class CourierStatistics {
    Long courierId;
    Long completedOrders;
    Long totalCost;
    Long earnings;
    BigDecimal rating;
    BigDecimal averageOrderCost;
    BigDecimal productivity;
}
