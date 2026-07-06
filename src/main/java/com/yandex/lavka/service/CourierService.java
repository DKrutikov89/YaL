package com.yandex.lavka.service;

import com.yandex.lavka.model.dto.CourierDto;
import com.yandex.lavka.model.dto.CreateCourierDto;
import com.yandex.lavka.model.entity.Courier;
import com.yandex.lavka.model.enums.CourierType;
import com.yandex.lavka.repository.CourierRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервис для работы с курьерами.
 * Использует CourierRepository для доступа к базе данных.
 */
@Service
public class CourierService {

    private static final Logger logger = LoggerFactory.getLogger(CourierService.class);

    // ============================================
    // Внедрение репозитория через конструктор
    // ============================================
    private final CourierRepository courierRepository;

    public CourierService(CourierRepository courierRepository) {
        this.courierRepository = courierRepository;
        logger.info("🚀 CourierService инициализирован с Repository");
    }

    // ============================================
    // 1. Базовые CRUD операции
    // ============================================

    /**
     * Создание списка курьеров.
     * Сохраняет всех курьеров в базу данных.
     */
    @Transactional
    public List<CourierDto> createCouriers(List<CreateCourierDto> createCourierDtos) {
        logger.info("📥 Начало создания курьеров. Количество: {}", createCourierDtos.size());

        // Конвертируем DTO → Entity
        List<Courier> couriers = createCourierDtos.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());

        // Сохраняем в БД
        List<Courier> savedCouriers = courierRepository.saveAll(couriers);

        // Конвертируем Entity → DTO
        List<CourierDto> result = savedCouriers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        logger.info("✅ Создание курьеров завершено. Создано: {}", result.size());
        return result;
    }

    /**
     * Получение всех курьеров с пагинацией.
     */
    @Transactional(readOnly = true)
    public List<CourierDto> getAllCouriers() {
        logger.debug("📋 Запрос всех курьеров");

        List<Courier> couriers = courierRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        return couriers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Получение курьеров с пагинацией.
     *
     * @param limit  количество записей на странице (по умолчанию 10)
     * @param offset смещение (по умолчанию 0)
     * @return список курьеров с пагинацией
     */
    @Transactional(readOnly = true)
    public List<CourierDto> getCouriersWithPagination(int limit, int offset) {
        // Значения по умолчанию
        int defaultLimit = 10;
        int defaultOffset = 0;

        // Применяем значения по умолчанию
        if (limit <= 0) limit = defaultLimit;
        if (offset < 0) offset = defaultOffset;

        logger.debug("📋 Запрос курьеров с пагинацией: limit={}, offset={}", limit, offset);

        // Создаем Pageable объект
        Pageable pageable = PageRequest.of(offset / limit, limit, Sort.by(Sort.Direction.ASC, "id"));

        // Получаем страницу из репозитория
        Page<Courier> page = courierRepository.findAll(pageable);

        return page.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Получение курьера по ID.
     */
    @Transactional(readOnly = true)
    public Optional<CourierDto> getCourierById(Long courierId) {
        logger.debug("🔍 Поиск курьера по ID: {}", courierId);

        return courierRepository.findById(courierId)
                .map(this::convertToDto);
    }

    /**
     * Проверка существования курьера.
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long courierId) {
        return courierRepository.existsById(courierId);
    }

    // ============================================
    // 2. Поисковые методы из Repository
    // ============================================

    /**
     * Поиск курьеров по типу.
     */
    @Transactional(readOnly = true)
    public List<CourierDto> findByCourierType(CourierType type) {
        logger.debug("🔍 Поиск курьеров по типу: {}", type);

        return courierRepository.findByCourierType(type).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Поиск курьеров по типу с пагинацией.
     */
    @Transactional(readOnly = true)
    public List<CourierDto> findByCourierType(CourierType type, int limit, int offset) {
        int defaultLimit = 10;
        int defaultOffset = 0;

        if (limit <= 0) limit = defaultLimit;
        if (offset < 0) offset = defaultOffset;

        Pageable pageable = PageRequest.of(offset / limit, limit, Sort.by(Sort.Direction.ASC, "id"));

        Page<Courier> page = courierRepository.findByCourierType(type, pageable);

        return page.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Поиск курьеров по региону.
     */
    @Transactional(readOnly = true)
    public List<CourierDto> findByRegion(Integer region) {
        logger.debug("🔍 Поиск курьеров по региону: {}", region);

        return courierRepository.findByRegionContaining(region).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Поиск курьеров по региону с пагинацией.
     */
    @Transactional(readOnly = true)
    public List<CourierDto> findByRegion(Integer region, int limit, int offset) {
        int defaultLimit = 10;
        int defaultOffset = 0;

        if (limit <= 0) limit = defaultLimit;
        if (offset < 0) offset = defaultOffset;

        Pageable pageable = PageRequest.of(offset / limit, limit, Sort.by(Sort.Direction.ASC, "id"));

        Page<Courier> page = courierRepository.findByRegionContaining(region, pageable);

        return page.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Поиск курьеров по типу и региону.
     */
    @Transactional(readOnly = true)
    public List<CourierDto> findByTypeAndRegion(CourierType type, Integer region) {
        logger.debug("🔍 Поиск курьеров по типу {} и региону {}", type, region);

        // Передаем type.name() вместо type (String вместо CourierType)
        return courierRepository.findByTypeAndRegion(type.name(), region).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // ============================================
    // 3. Статистика
    // ============================================

    /**
     * Получение общего количества курьеров.
     */
    @Transactional(readOnly = true)
    public long countCouriers() {
        return courierRepository.count();
    }

    /**
     * Получение всех типов курьеров.
     */
    @Transactional(readOnly = true)
    public List<CourierType> findAllCourierTypes() {
        return courierRepository.findAllCourierTypes();
    }

    // ============================================
    // 4. Методы конвертации: Entity ↔ DTO
    // ============================================

    /**
     * Конвертация CreateCourierDto → Courier (Entity)
     */
    private Courier convertToEntity(CreateCourierDto dto) {
        Courier courier = new Courier();
        courier.setCourierType(dto.getCourierType());
        courier.setRegions(dto.getRegions());
        courier.setWorkingHours(dto.getWorkingHours());
        // createdAt и updatedAt устанавливаются автоматически
        return courier;
    }

    /**
     * Конвертация Courier (Entity) → CourierDto
     */
    private CourierDto convertToDto(Courier entity) {
        return new CourierDto(
                entity.getId(),
                entity.getCourierType(),
                entity.getRegions(),
                entity.getWorkingHours()
        );
    }

    /**
     * Конвертация списка Entities → список DTOs
     */
    private List<CourierDto> convertToDtoList(List<Courier> entities) {
        return entities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}