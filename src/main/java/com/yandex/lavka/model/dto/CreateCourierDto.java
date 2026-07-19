package com.yandex.lavka.model.dto;
// 1. CreateCourierDto - Для создания курьера.
// DTO (Data Transfer Object) — это объект для передачи данных между слоями приложения.
// В данном случае — между сервером и клиентом.
import com.yandex.lavka.model.enums.CourierType;
import com.yandex.lavka.validation.ValidRegions;
import com.yandex.lavka.validation.ValidWorkingHours;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data // @Data Создаёт геттеры, сеттеры, toString, equals, hash
@NoArgsConstructor     // @NoArgsConstructor Создаёт пустой конструктор: new CreateCourierDto()
@AllArgsConstructor     // @AllArgsConstructor Создаёт конструктор со всеми полями: new CreateCourierDto(тип, регионы, часы)
public class CreateCourierDto {
    // @NotNull проверка: поле courierType обязательно должно быть заполнено (нельзя оставить пустым)
    @NotNull(message = "{validation.courier.type.required}")
    private CourierType courierType;      // courierType это перечисление (enum) с вариантами

    // @NotEmpty — список не должен быть пустым (хотя бы один район)
    @NotEmpty(message = "{validation.regions.required}")
    @ValidRegions
    // @Positive — каждое число в списке должно быть положительным (1, 2, 3... нельзя 0 или -5)
    private List<@Positive(message = "{validation.regions.positive}") Integer> regions;

    // @NotEmpty — нельзя сдать пустой график
    @NotEmpty(message = "{validation.working-hours.required}")
    @ValidWorkingHours
    private List<@Pattern(
            // @Pattern(regexp=...) — каждое время должно строго соответствовать формату,
            // описанному регулярным выражением
            regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]-([0-1]?[0-9]|2[0-3]):[0-5][0-9]$",
            message = "{validation.working-hours.format}"
    ) String> workingHours;

    // ^                      - начало строки
    //([0-1]?[0-9]|2[0-3])  - часы: от 00 до 23
    //:                      - двоеточие
    //[0-5][0-9]            - минуты: от 00 до 59
    //-                      - дефис (разделитель)
    //([0-1]?[0-9]|2[0-3])  - снова часы
    //:                      - двоеточие
    //[0-5][0-9]            - снова минуты
    //$                      - конец строки

    //______________________________________________________________________________________________________

    // // ✅ Велокурьер только в районе 5, полный день
    //CreateCourierDto dto2 = new CreateCourierDto(
    //    CourierType.BIKE,
    //    List.of(5),
    //    List.of("10:00-20:00")

    // Жизненный цикл этого DTO
    //Клиент (фронтенд/другая система)
    //    ↓
    //Присылает JSON: { "courierType": "BIKE", "regions": [5], "workingHours": ["10:00-20:00"] }
    //    ↓
    //Spring автоматически превращает JSON в объект CreateCourierDto
    //    ↓
    //Срабатывают проверки (@NotNull, @NotEmpty, @Pattern...)
    //    ↓
    //Если всё ок — DTO передаётся в сервис для создания реального курьера
    //    ↓
    //Если ошибка — клиент получает понятное сообщение об ошибке

    // Далее ------ > Сервис или Ошибка.
}
