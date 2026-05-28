package com.yandex.lavka.model.dto;

import com.yandex.lavka.model.enums.CourierType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCourierDto {

    @NotNull(message = "Courier type is required")
    private CourierType courierType;

    @NotEmpty(message = "Regions list cannot be empty")
    private List<@Positive(message = "Region must be positive") Integer> regions;

    @NotEmpty(message = "Working hours list cannot be empty")
    private List<@Pattern(
            regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]-([0-1]?[0-9]|2[0-3]):[0-5][0-9]$",
            message = "Working hours must be in format HH:MM-HH:MM"
    ) String> workingHours;
}