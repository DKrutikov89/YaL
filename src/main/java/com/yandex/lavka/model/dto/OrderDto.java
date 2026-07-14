package com.yandex.lavka.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private Long orderId;
    private BigDecimal weight;
    private Integer region;
    private List<String> deliveryHours;
    private Integer cost;
    private Long courierId;
    private LocalDateTime completedTime;
}
