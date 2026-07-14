package com.yandex.lavka.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompleteOrderRequest {

    @NotEmpty(message = "Complete info list cannot be empty")
    private List<@Valid CompleteOrderDto> completeInfo;
}
