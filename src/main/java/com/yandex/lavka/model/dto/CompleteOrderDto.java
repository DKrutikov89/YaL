package com.yandex.lavka.model.dto;

import com.yandex.lavka.validation.ValidOrderCompletion;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@ValidOrderCompletion
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompleteOrderDto {

    @NotNull(message = "{validation.courier-id.required}")
    @Positive(message = "{validation.courier-id.positive}")
    private Long courierId;

    @NotNull(message = "{validation.order-id.required}")
    @Positive(message = "{validation.order-id.positive}")
    private Long orderId;

    @NotNull(message = "{validation.complete-time.required}")
    private LocalDateTime completeTime;
}
