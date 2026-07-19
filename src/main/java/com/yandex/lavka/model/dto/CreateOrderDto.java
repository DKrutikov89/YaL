package com.yandex.lavka.model.dto;

import com.yandex.lavka.validation.ValidWeight;
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

    @NotNull(message = "{validation.weight.required}")
    @ValidWeight
    private BigDecimal weight;

    @NotNull(message = "{validation.region.required}")
    @Positive(message = "{validation.regions.positive}")
    private Integer region;

    @NotEmpty(message = "{validation.delivery-hours.required}")
    @ValidWorkingHours
    private List<String> deliveryHours;

    @NotNull(message = "{validation.cost.required}")
    @Positive(message = "{validation.cost.positive}")
    private Integer cost;
}
