package com.yandex.lavka.model.dto;

import com.yandex.lavka.model.enums.CourierType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// 2.4 DTO для ответа

@Data
//Аннотации Lombok
//@Data — самая полезная аннотация. Она автоматически генерирует:
//Геттеры для всех полей (чтобы читать данные)
//Сеттеры для всех полей (чтобы изменять данные)
//toString() (для удобного вывода в консоль)
//equals() и hashCode() (для сравнения объектов)
@NoArgsConstructor // @NoArgsConstructor — создает пустой конструктор
@AllArgsConstructor // @AllArgsConstructor — создает конструктор со всеми полями:
public class CourierDto {

    private Long courierId;
    private CourierType courierType;
    private List<Integer> regions;
    private List<String> workingHours;
}

// DTO-классы
// DTO = Data Transfer Object.
// Проще говоря: это классы для переноса данных между слоями и по сети.

// Они нужны, чтобы:
//- удобно читать JSON;
//- удобно отдавать JSON;
//- не смешивать транспортный формат с внутренней логикой.

//Почему DTO — это хорошо
//Если использовать один и тот же класс для всего подряд, получится путаница:
//- входные данные;
//- внутренние данные;
//- ответы клиенту.
//DTO помогают всё это разделить.

// Зачем нужен этот класс
//Это уже **готовый курьер**, который:
//- хранится в памяти;
//- возвращается в ответах API.

// Разбор:
//- `courierId` — идентификатор;
//- `courierType` — тип курьера;
//- `regions` — список регионов;
//- `workingHours` — рабочие интервалы.

//### Важное различие с `CreateCourierDto`
//`CreateCourierDto`:
//- приходит от клиента;
//- не содержит `id`.
//
//`CourierDto`:
//- используется после создания;
//- содержит `id`.
//То есть это два разных этапа жизни данных.