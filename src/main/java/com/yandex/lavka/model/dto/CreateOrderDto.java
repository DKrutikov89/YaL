package com.yandex.lavka.model.dto;

import com.yandex.lavka.validation.ValidWorkingHours;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderDto {

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    private BigDecimal weight;

    @NotNull(message = "Region is required")
    @Positive(message = "Region must be positive")
    private Integer region;

    @NotEmpty(message = "Delivery hours list cannot be empty")
    @ValidWorkingHours
    private List<String> deliveryHours;

    @NotNull(message = "Cost is required")
    @Positive(message = "Cost must be positive")
    private Integer cost;
}
