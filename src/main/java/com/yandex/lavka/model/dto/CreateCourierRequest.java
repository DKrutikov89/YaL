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
public class CreateCourierRequest {
    @NotEmpty(message = "Couriers list cannot be empty")
    private List<@Valid CreateCourierDto> couriers;

    // Внутри request лежит список CreateCourierDto
// couriers = [CreateCourierDto{type=AUTO, regions=[1,2], hours=["09:00-18:00"]}]
}

// 2.3 Request DTO для создания нескольких курьеров
// @Data, @NoArgsConstructor, @AllArgsConstructor — аннотации Lombok (библиотека, чтобы не писать скучный код вручную)
// @Data генерирует геттеры, сеттеры, toString, equals, hashCode
// @NoArgsConstructor создаёт конструктор без параметров
// @AllArgsConstructor создаёт конструктор со всеми полями
// @NotEmpty — проверка: список не должен быть null и не должен быть пустым (хотя бы 1 курьер)
// @Valid — говорит: "проверь каждого курьера в списке по его собственным правилам"
// список курьеров (каждый курьер — это объект типа CreateCourierDto)