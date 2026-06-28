package com.yandex.lavka.service;

import com.yandex.lavka.model.dto.CourierDto;
import com.yandex.lavka.model.dto.CreateCourierDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


// Что происходит:
//Сервис берёт каждого курьера из запроса.
//Генерирует для него уникальный ID (1, 2, 3...)
//Сохраняет в HashMap (как в виртуальной базе данных)
//Возвращает список созданных курьеров с их новыми ID

// Сервис — это место, где живёт логика приложения.
//В этом проекте он:
//- создаёт курьеров;
//- хранит их в памяти;
//- отдаёт список;
//- ищет по `id`;
//- генерирует новые `id`.

// @Service`
// Говорит Spring:
//- это сервисный компонент;
//- нужно создать его объект как Spring-бин;
//- его можно внедрять в другие классы.

@Service // ← Помечаем как сервисный компонент
public class CourierService {
    //  ЛОГГЕР - добавляем!
    private static final Logger logger = LoggerFactory.getLogger(CourierService.class);

    // In-memory хранилище курьеров (временно, до подключения БД)
    private final ConcurrentHashMap<Long, CourierDto> couriers = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1); // Генератор ID

    /**
     * Создание списка курьеров
     */
    public List<CourierDto> createCouriers(List<CreateCourierDto> createCourierDtos) {
        List<CourierDto> createdCouriers = new ArrayList<>();

        for (CreateCourierDto createDto : createCourierDtos) {
            // 1. Превращаем CreateCourierDto в CourierDto (с ID)
            CourierDto courierDto = convertToDto(createDto);
            // 2. Сохраняем в хранилище
            couriers.put(courierDto.getCourierId(), courierDto);
            // 3. Добавляем в список ответа
            createdCouriers.add(courierDto);
            logger.debug("✅ Создан курьер: id={}, type={}, regions={}, hours={}",
                    courierDto.getCourierId(),
                    courierDto.getCourierType(),
                    courierDto.getRegions(),
                    courierDto.getWorkingHours()
            );
        }

        logger.info("✅ Создание курьеров завершено. Создано: {}", createdCouriers.size());
        return createdCouriers;

        // `couriers`
//Это хранилище в памяти.
//Ключ: `Long` — `id` курьера.
//Значение: `CourierDto` — сам курьер.
//Почему `ConcurrentHashMap`, а не обычный `HashMap`:
//- веб-приложение может обрабатывать несколько запросов одновременно;
//- `ConcurrentHashMap` безопаснее для многопоточной среды.

        // `idGenerator`
//Это счётчик для новых `id`.
//`new AtomicLong(1)` означает:
//- начинаем с `1`;
//- каждый новый курьер получит следующее число.
//Почему `AtomicLong`:
//- тоже безопасен для многопоточной работы;
//- несколько потоков не сломают счётчик так легко, как обычный `long`.

    }

    //### Метод `createCouriers`
//public List<CourierDto> createCouriers(List<CreateCourierDto> createCourierDtos) {
//На вход:
//- список данных для создания.
//На выход:
//- список уже созданных курьеров с `id`.
//Дальше:
//List<CourierDto> createdCouriers = new ArrayList<>();
//Создаётся пустой список результата.
//for (CreateCourierDto createDto : createCourierDtos) {
//Цикл по каждому входному объекту.
//CourierDto courierDto = convertToDto(createDto);
//Преобразуем входной объект в полноценный объект курьера.
//Почему отдельный метод хорошо:
//- логика преобразования вынесена в одно место;
//- код метода `createCouriers` стал чище.
// couriers.put(courierDto.getCourierId(), courierDto);
//Кладём курьера в карту:
//- ключ = `id`;
//- значение = объект курьера.
//createdCouriers.add(courierDto);
//Добавляем курьера в список ответа.
//После цикла:
//return createdCouriers;
//Возвращаем список созданных объектов.

    /**
     * Получение всех курьеров
     */
    public List<CourierDto> getAllCouriers() {
        logger.debug("📋 Запрос всех курьеров. Всего в базе: {}", couriers.size());
        List<CourierDto> result = couriers.values().stream()
                .sorted(Comparator.comparing(CourierDto::getCourierId))
                .toList();

        logger.debug("📋 Возвращено курьеров: {}", result.size());
        return result;
        //Разбор:
//
//- `couriers.values()` — взять все объекты из карты;
//- `.stream()` — перейти к Stream API;
//- `.sorted(...)` — отсортировать;
//- `Comparator.comparing(CourierDto::getCourierId)` — сортировать по `courierId`;
//- `.toList()` — собрать обратно в список.

        //Почему сортировка тут полезна:
//
//- `Map` не гарантирует удобный порядок;
//- так ответ становится предсказуемым;
//- тесты и клиентам проще работать.
//

    }

    /**
     * Получение курьера по ID
     */
    public Optional<CourierDto> getCourierById(Long courierId) {
        logger.debug("🔍 Поиск курьера по ID: {}", courierId);

        Optional<CourierDto> result = Optional.ofNullable(couriers.get(courierId));

        if (result.isPresent()) {
            logger.debug("✅ Курьер найден: id={}, type={}",
                    result.get().getCourierId(),
                    result.get().getCourierType()
            );
        } else {
            logger.warn("⚠️ Курьер не найден: id={}", courierId);
        }

        return result;
    }

        //### Метод `getCourierById`
//Что происходит:
//- из карты пытаемся взять курьера по `id`;
//- если ничего нет, `couriers.get(...)` вернёт `null`;
//- `Optional.ofNullable(...)` аккуратно заворачивает результат в `Optional`.


    /**
     * Проверка существования курьера
     */
    public boolean existsById(Long courierId) {
        boolean exists = couriers.containsKey(courierId);
        logger.debug("🔍 Проверка существования курьера id={}: {}", courierId, exists);
        return exists;

        //### Метод `existsById`
//Проверяет, есть ли курьер.
//Сейчас этот метод не используется.
//Но он может пригодиться позже.

//Например:
//- перед обновлением;
//- перед удалением;
//- в более сложной валидации.
    }

    /**
     * Конвертация CreateCourierDto в CourierDto
     */
    private CourierDto convertToDto(CreateCourierDto createDto) {
        Long newId = idGenerator.getAndIncrement();
        logger.debug("🔄 Конвертация: CreateCourierDto → CourierDto с id={}", newId);

        return new CourierDto(
                newId,
                createDto.getCourierType(),
                new ArrayList<>(createDto.getRegions()),
                new ArrayList<>(createDto.getWorkingHours())
        );
    }
}



//### Приватный метод `convertToDto`
//private CourierDto convertToDto(CreateCourierDto createDto) {
//`private` значит:

//- использовать можно только внутри `CourierService`.

//Long newId = idGenerator.getAndIncrement();
//Очень важная строка.

//Что делает:
//- берёт текущее значение счётчика;
//- возвращает его;
//- потом увеличивает счётчик на 1.

//Например:
//- сначала вернёт `1`;
//- потом `2`;
//- потом `3`.

//Дальше:
//return new CourierDto(
//        newId,
//        createDto.getCourierType(),
//        new ArrayList<>(createDto.getRegions()),
//        new ArrayList<>(createDto.getWorkingHours())
//);

//Создаётся новый объект `CourierDto`
//Что туда кладётся:
//- новый `id`;
//- тип курьера;
//- список регионов;
//- список рабочих часов.

//Почему списки копируются через `new ArrayList<>(...)`:
//- чтобы не хранить прямую ссылку на внешний список;
//- это чуть безопаснее;
//- меньше шанс, что внешний код потом случайно изменит внутренние данные.

//### Главная мысль по сервису
//Этот сервис сейчас играет роль мини-базы данных и бизнес-логики одновременно.
//Позже, когда появится настоящая БД:
//- карта исчезнет;
//- вместо неё будет repository;
//- логика создания может остаться в сервисе.