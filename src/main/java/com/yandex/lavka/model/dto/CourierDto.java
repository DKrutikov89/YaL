package com.yandex.lavka.model.dto;

import com.yandex.lavka.model.enums.CourierType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourierDto {

    private Long courierId;
    private CourierType courierType;
    private List<Integer> regions;
    private List<String> workingHours;
}