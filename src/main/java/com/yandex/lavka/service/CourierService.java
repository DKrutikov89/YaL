package com.yandex.lavka.service;

import com.yandex.lavka.model.dto.CourierDto;
import com.yandex.lavka.model.dto.CreateCourierDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CourierService {

    // In-memory хранилище (временно, до подключения БД)
    private final ConcurrentHashMap<Long, CourierDto> couriers = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * Создание списка курьеров
     */
    public List<CourierDto> createCouriers(List<CreateCourierDto> createCourierDtos) {
        List<CourierDto> createdCouriers = new ArrayList<>();

        for (CreateCourierDto createDto : createCourierDtos) {
            CourierDto courierDto = convertToDto(createDto);
            couriers.put(courierDto.getCourierId(), courierDto);
            createdCouriers.add(courierDto);
        }

        return createdCouriers;
    }

    /**
     * Получение всех курьеров
     */
    public List<CourierDto> getAllCouriers() {
        return couriers.values().stream()
                .sorted(Comparator.comparing(CourierDto::getCourierId))
                .toList();
    }

    /**
     * Получение курьера по ID
     */
    public Optional<CourierDto> getCourierById(Long courierId) {
        return Optional.ofNullable(couriers.get(courierId));
    }

    /**
     * Проверка существования курьера
     */
    public boolean existsById(Long courierId) {
        return couriers.containsKey(courierId);
    }

    /**
     * Конвертация CreateCourierDto в CourierDto
     */
    private CourierDto convertToDto(CreateCourierDto createDto) {
        Long newId = idGenerator.getAndIncrement();
        return new CourierDto(
                newId,
                createDto.getCourierType(),
                new ArrayList<>(createDto.getRegions()),
                new ArrayList<>(createDto.getWorkingHours())
        );
    }
}
