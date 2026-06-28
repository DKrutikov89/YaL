package com.yandex.lavka.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

// Это фиксированный набор констант (типов курьеров): пеший, вело, авто.
// Enum-константы по умолчанию имеют имя (FOOT, BIKE, AUTO).
// Но при передаче данных в JSON (например, через API) часто нужно отправлять именно строку, а не имя enum.
// Например, CourierType type = CourierType.BIKE;
// System.out.println(type.name()); // Выведет: "BIKE" (это имя константы)
// System.out.println(type.getValue()); // Выведет: "BIKE" (это наша строка)
public enum CourierType {
    FOOT("FOOT"),
    BIKE("BIKE"),
    AUTO("AUTO");

    // Поле Хранит строковое представление enum
    // Нужно для сериализации (превращения в JSON) и десериализации (из JSON обратно).
    private final String value;


    // При создании enum-констант FOOT("FOOT")
    // — строка "FOOT" передается в конструктор и сохраняется в поле value.
    CourierType(String value) {
        this.value = value;
    }

    // Пользователь может вызвать CourierType.FOOT.getValue() — получит строку "FOOT".
    // Превращается в JSON: "FOOT"
    // Это пометка для Jackson (библиотека для работы с JSON).
    // Она говорит: «Когда будешь превращать этот enum в JSON — используй строку value».
    //Пример:
    // Было: CourierType.AUTO
    // Стало в JSON: "AUTO"
    @JsonValue
    public String getValue() {
        return value;
    }

    // Это пометка: «Когда получишь JSON со строкой, найди по ней правильный enum».
    // Получили из JSON: "BIKE"
    // Нужно превратить в: CourierType.BIKE
    @JsonCreator
    public static CourierType fromString(String value) {
        for (CourierType type : CourierType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown courier type: " + value);
    }
}


// Конструктор
//
//```java
//CourierType(String value) {
//    this.value = value;
//}
//```
//
//При создании каждой константы внутрь записывается строка.
//
//### `@JsonValue`
//
//```java
//@JsonValue
//public String getValue() {
//    return value;
//}
//```
//
//Это говорит Jackson:
//
//- когда enum превращается в JSON, используй именно `value`.
//
//То есть в JSON пойдёт строка:
//
//- `"FOOT"`
//- `"BIKE"`
//- `"AUTO"`
//
//### `@JsonCreator`
//
//```java
//@JsonCreator
//public static CourierType fromString(String value) {
//```
//
//Это говорит Jackson:
//
//- когда из JSON приходит строка, вот так нужно превращать её в enum.
//
//Цикл:
//
//```java
//for (CourierType type : CourierType.values()) {
//    if (type.value.equalsIgnoreCase(value)) {
//        return type;
//    }
//}
//```
//
//Что делает:
//
//- проходит по всем возможным типам;
//- сравнивает входную строку;
//- `equalsIgnoreCase` игнорирует регистр букв.
//
//То есть:
//
//- `"AUTO"` подойдёт;
//- `"auto"` тоже подойдёт;
//- `"AuTo"` тоже подойдёт.
//
//Если ничего не подошло:
//
//```java
//throw new IllegalArgumentException("Unknown courier type: " + value);
//```
//
//Выбрасывается исключение.
//
//Потом это исключение поймает `GlobalExceptionHandler` и вернёт клиенту `400 Bad Request`.
//
//### Почему это решение хорошее
//
//- строгий набор значений;
//- удобная сериализация в JSON;
//- удобное чтение из JSON;
//- понятная ошибка при плохом значении.
