package com.yandex.lavka.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompleteOrderDto {

    @NotNull(message = "Courier id is required")
    @Positive(message = "Courier id must be positive")
    private Long courierId;

    @NotNull(message = "Order id is required")
    @Positive(message = "Order id must be positive")
    private Long orderId;

    @NotNull(message = "Complete time is required")
    private LocalDateTime completeTime;
}
