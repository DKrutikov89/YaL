package com.yandex.lavka.repository;

import com.yandex.lavka.model.entity.Courier;
import com.yandex.lavka.model.enums.CourierType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностью Courier.
 * Расширяет JpaRepository, предоставляя CRUD операции.
 */
@Repository
public interface CourierRepository extends JpaRepository<Courier, Long> {

    // ============================================
    // 1. Базовые методы (уже есть в JpaRepository)
    // ============================================
    // ✅ findAll() - получить всех курьеров
    // ✅ findById(Long id) - найти по ID
    // ✅ save(Courier courier) - сохранить
    // ✅ deleteById(Long id) - удалить по ID
    // ✅ existsById(Long id) - проверить существование
    // ✅ count() - количество записей

    // ============================================
    // 2. Query by method name (автоматические)
    // ============================================

    /**
     * Поиск курьеров по типу.
     * Spring Data JPA автоматически создаст запрос по имени метода.
     * SELECT * FROM lavka_schema.couriers WHERE courier_type = ?
     */
    List<Courier> findByCourierType(CourierType courierType);

    /**
     * Поиск курьеров по типу с пагинацией.
     */
    Page<Courier> findByCourierType(CourierType courierType, Pageable pageable);

    /**
     * Поиск курьеров по типу, отсортированных по ID.
     */
    List<Courier> findByCourierTypeOrderByIdAsc(CourierType courierType);

    /**
     * Проверка существования курьера по типу.
     */
    boolean existsByCourierType(CourierType courierType);

    /**
     * Количество курьеров по типу.
     */
    long countByCourierType(CourierType courierType);

    // ============================================
    // 3. Поиск по массивам (PostgreSQL)
    // ============================================

    /**
     * Поиск курьеров, работающих в определенном регионе.
     * Использует PostgreSQL оператор ANY для массивов.
     * SELECT * FROM lavka_schema.couriers WHERE :region = ANY(regions)
     */
    @Query(value = "SELECT * FROM lavka_schema.couriers WHERE :region = ANY(regions)",
            nativeQuery = true)
    List<Courier> findByRegionContaining(@Param("region") Integer region);

    /**
     * Поиск курьеров, работающих в определенном регионе с пагинацией.
     */
    @Query(value = "SELECT * FROM lavka_schema.couriers WHERE :region = ANY(regions)",
            countQuery = "SELECT COUNT(*) FROM lavka_schema.couriers WHERE :region = ANY(regions)",
            nativeQuery = true)
    Page<Courier> findByRegionContaining(@Param("region") Integer region, Pageable pageable);

    /**
     * Поиск курьеров, работающих в нескольких регионах.
     * Использует PostgreSQL оператор && (пересечение массивов).
     * SELECT * FROM lavka_schema.couriers WHERE regions && ARRAY[:regions]
     */
    @Query(value = "SELECT * FROM lavka_schema.couriers WHERE regions && ARRAY[:regions]",
            nativeQuery = true)
    List<Courier> findByRegionsIn(@Param("regions") Integer[] regions);

    // ============================================
    // 4. Сложные запросы с @Query (JPQL)
    // ============================================

    /**
     * Поиск курьеров по частичному совпадению в рабочих часах.
     * Использует PostgreSQL оператор @> (содержит массив).
     * SELECT * FROM lavka_schema.couriers WHERE working_hours @> ARRAY[:hour]::TEXT[]
     */
    @Query(value = "SELECT * FROM lavka_schema.couriers WHERE working_hours @> ARRAY[:hour]::TEXT[]",
            nativeQuery = true)
    List<Courier> findByWorkingHoursContaining(@Param("hour") String hour);

    /**
     * Поиск курьеров по типу и региону (JPQL).
     */
    @Query("SELECT c FROM Courier c WHERE c.courierType = :type AND :region MEMBER OF c.regions")
    List<Courier> findByTypeAndRegion(@Param("type") CourierType type,
                                      @Param("region") Integer region);

    /**
     * Поиск курьеров с пагинацией и сортировкой.
     */
    @Query("SELECT c FROM Courier c ORDER BY c.id ASC")
    Page<Courier> findAllWithPagination(Pageable pageable);

    // ============================================
    // 5. Кастомные методы для статистики
    // ============================================

    /**
     * Получение всех типов курьеров в базе.
     */
    @Query("SELECT DISTINCT c.courierType FROM Courier c")
    List<CourierType> findAllCourierTypes();

    /**
     * Количество курьеров по типу (JPQL вариант).
     */
    @Query("SELECT COUNT(c) FROM Courier c WHERE c.courierType = :type")
    long countByType(@Param("type") CourierType type);

    /**
     * Поиск курьеров с максимальным количеством регионов.
     */
    @Query(value = "SELECT * FROM lavka_schema.couriers ORDER BY array_length(regions, 1) DESC LIMIT 1",
            nativeQuery = true)
    Optional<Courier> findWithMostRegions();

    // ============================================
    // 6. Методы для проверки существования
    // ============================================

    /**
     * Проверка, существует ли курьер с данным типом.
     */
    boolean existsByCourierType(String courierType);

    /**
     * Проверка, работает ли курьер в регионе.
     */
    @Query(value = "SELECT COUNT(*) > 0 FROM lavka_schema.couriers WHERE id = :courierId AND :region = ANY(regions)",
            nativeQuery = true)
    boolean isCourierInRegion(@Param("courierId") Long courierId,
                              @Param("region") Integer region);
}