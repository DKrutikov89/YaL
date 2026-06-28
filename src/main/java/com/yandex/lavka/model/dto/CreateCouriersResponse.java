package com.yandex.lavka.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCouriersResponse {

    private List<CourierDto> couriers;
}

// это как конверт с ответом от сервера.
// Когда вы добавляете новых курьеров в систему, сервер должен как-то ответить вам:
// "ОК, я добавил этих курьеров, вот они".

// Это формат ответа после создания курьеров.
//
//Содержит:
//
//- список созданных `CourierDto`.
//
//### Поле
//
//```java
//private List<CourierDto> couriers;
//```
//
//То есть ответ будет таким:
//
//```json
//{
//  "couriers": [
//    {
//      "courier_id": 1,
//      "courier_type": "AUTO",
//      "regions": [1, 2],
//      "working_hours": ["09:00-18:00"]
//    }
//  ]
//}
//```
//
//### Почему не вернуть просто список
//
//Потому что обёртка ответа даёт гибкость.
//
//Позже можно добавить:
//
//- `count`;
//- `errors`;
//- `meta`;
//- служебные поля.
//
//И не менять тип ответа слишком резко.