package com.yandex.lavka.controller;

import com.yandex.lavka.exception.CourierNotFoundException;
import com.yandex.lavka.model.dto.CourierDto;
import com.yandex.lavka.model.dto.CreateCourierRequest;
import com.yandex.lavka.model.dto.CreateCouriersResponse;
import com.yandex.lavka.service.CourierService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // методы этого класса возвращают данные прямо в HTTP-ответ; Spring автоматически сериализует Java-объекты в JSON.
@RequestMapping("/couriers") //  этот контроллер отвечает за URL, начинающиеся с /couriers
// ← Все методы этого контроллера начинаются с /couriers
public class CourierController {

    // 🔥 ЛОГГЕР - добавляем!
    private static final Logger logger = LoggerFactory.getLogger(CourierController.class);
    private final CourierService courierService; // Сильная зависимость.

    public CourierController(CourierService courierService) {
        this.courierService = courierService;
        logger.info("CourierController инициализирован");
    }
    // - Spring видит, что контроллеру нужен `CourierService`;
    //- находит бин `CourierService`;
    //- сам передаёт его в конструктор.

    /**
     * POST /couriers - Создание курьеров
     */
    @PostMapping  // ← Этот метод отвечает на POST-запрос /couriers
    //- `ResponseEntity<CreateCouriersResponse>` — вернётся HTTP-ответ с телом типа `CreateCouriersResponse`;
    public ResponseEntity<CreateCouriersResponse> createCouriers(
            @Valid @RequestBody CreateCourierRequest request)
    {
        logger.info("Получен POST-запрос на создание курьеров");
        logger.debug("Данные запроса: {}", request);

        long startTime = System.currentTimeMillis();

        // courierService - Объект сервиса, который содержит бизнес-логику.  Это как "менеджер по работе с курьерами"
        // Он умеет создавать, искать, удалять курьеров. Spring сам создал этот объект и передал его в контроллер
        // Он знает всех курьеров и умеет с ними работать

        //  .createCouriers() Что это: Вызов метода (создать курьеров)
        //  Он принимает на вход список данных для создания.
        // Возвращает список уже созданных курьеров с ID у объекта courierService.
        // // Сервис получает список CreateCourierDto без ID
        // 5: Сервис создает курьеров с ID и возвращает их
        // Шаг 6: Сохраняем результат в переменную

        List<CourierDto> createdCouriers = courierService.createCouriers(request.getCouriers());
        // - из `request` достаётся список курьеров;
        // request.getCouriers()
        //Что это: Получение данных из объекта запроса.
        CreateCouriersResponse response = new CreateCouriersResponse(createdCouriers);

        long duration = System.currentTimeMillis() - startTime;
        logger.info("Курьеры созданы. Количество: {}, Время: {}ms", createdCouriers.size(), duration);

        //- `@Valid` — перед вызовом метода проверить объект по правилам валидации.
        //- `@RequestBody` — взять JSON из тела запроса и превратить в Java-объект;
        // Spring смотрит на аннотацию @RequestBody и понимает, что нужно превратить JSON в объект типа CreateCourierRequest:
        return ResponseEntity.ok(response);
    }


    //- контроллер передаёт его сервису;
    //- сервис создаёт реальные объекты с `id`.

    // Создаётся объект ответа.
    //Почему не вернуть просто список:
    //- потому что API часто удобнее строить через явную оболочку;
    //- можно позже добавить новые поля, не ломая формат сразу.

    //return ResponseEntity.ok(response);
    //Это значит:
    //- вернуть HTTP `200 OK`;
    //- положить `response` в тело ответа.

    /**
     * GET /couriers - Получение всех курьеров
     */
    @GetMapping
    public ResponseEntity<List<CourierDto>> getAllCouriers() {
        logger.info("Получен GET-запрос на получение всех курьеров");

        long startTime = System.currentTimeMillis();

        List<CourierDto> couriers = courierService.getAllCouriers();

        long duration = System.currentTimeMillis() - startTime;
        logger.info("Все курьеры получены. Количество: {}, Время: {}ms", couriers.size(), duration);

        return ResponseEntity.ok(couriers);
        // - получает из сервиса всех курьеров;
        //- возвращает их клиенту.
    }

    /**
     * GET /couriers/{courier_id} - Получение курьера по ID
     */
    @GetMapping("/{courier_id}") // Это число поместится в аргументы этого метода, @PathVariable с помощью извлечем и получим доступ внутри метода.
    public ResponseEntity<CourierDto> getCourierById(@PathVariable("courier_id") Long courierId) {

        logger.info("Получен GET-запрос на получение курьера по ID: {}", courierId);

        long startTime = System.currentTimeMillis();
        CourierDto courier = courierService.getCourierById(courierId)
                .orElseThrow(() -> new CourierNotFoundException(courierId));

        long duration = System.currentTimeMillis() - startTime;
        logger.info("Курьер найден: id={}, type={}, Время: {}ms",
                courier.getCourierId(),
                courier.getCourierType(),
                duration
        );

        return ResponseEntity.ok(courier);
    }
    //  `@GetMapping("/{courier_id}")` — это путь с переменной;
    //- `@PathVariable("courier_id")` — взять кусок из URL и передать в параметр `courierId`;
    //- `Long courierId` — тип значения.
    //Если запрос:
    //`GET /couriers/5`
    //то: `courier_id = 5`;
    //- параметр `courierId` получит значение `5`.

    // - сервис возвращает `Optional<CourierDto>`;
    //- если значение есть, оно извлекается;
    //- если значения нет, бросается `CourierNotFoundException`.
    // Почему используется `Optional`:
    //- он явно показывает, что объект может отсутствовать;
    //- это лучше, чем молча вернуть `null`.

    // Если курьер найден, возвращаем его с кодом `200`.

    // Идея всего контроллера
    //Контроллер сам почти ничего “умного” не делает.
    //И это хорошо.
    //Его задача:
    //- принять;
    //- передать;
    //- вернуть.
    //Основная логика должна жить в сервисе.
}

// @RestController Сокращение кода. Чтобы каждый метод возвращал данные - т.е. был с аннотацией @ResponseBody.
// @RestController == @Controller над классом + @ResponseBody над каждым методом.
// @RestController Эта аннотация говорит Spring, что этот класс будет обрабатывать веб-запросы.
// @RequestMapping("/couriers") В методы этого контроллера попадем по адресу /couriers.
// @RequestBody - не возвращаем названия для представления (шаблоны для браузера из Темплейтс).
// Возвращаем данные в этом методе.