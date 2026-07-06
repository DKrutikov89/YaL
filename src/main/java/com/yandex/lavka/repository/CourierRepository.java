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

@Repository
public interface CourierRepository extends JpaRepository<Courier, Long> {

    // ============================================
    // Query by method name (автоматические)
    // ============================================

    List<Courier> findByCourierType(CourierType courierType);

    Page<Courier> findByCourierType(CourierType courierType, Pageable pageable);

    List<Courier> findByCourierTypeOrderByIdAsc(CourierType courierType);

    boolean existsByCourierType(CourierType courierType);

    long countByCourierType(CourierType courierType);

    // ============================================
    // Поиск по массивам (PostgreSQL)
    // ============================================

    @Query(value = "SELECT * FROM lavka_schema.couriers WHERE :region = ANY(regions)",
            nativeQuery = true)
    List<Courier> findByRegionContaining(@Param("region") Integer region);

    @Query(value = "SELECT * FROM lavka_schema.couriers WHERE :region = ANY(regions)",
            countQuery = "SELECT COUNT(*) FROM lavka_schema.couriers WHERE :region = ANY(regions)",
            nativeQuery = true)
    Page<Courier> findByRegionContaining(@Param("region") Integer region, Pageable pageable);

    @Query(value = "SELECT * FROM lavka_schema.couriers WHERE regions && ARRAY[:regions]",
            nativeQuery = true)
    List<Courier> findByRegionsIn(@Param("regions") Integer[] regions);

    // ============================================
    // Сложные запросы
    // ============================================

    @Query(value = "SELECT * FROM lavka_schema.couriers WHERE working_hours @> ARRAY[:hour]::TEXT[]",
            nativeQuery = true)
    List<Courier> findByWorkingHoursContaining(@Param("hour") String hour);

    /**
     * 🔥 ИСПРАВЛЕННЫЙ МЕТОД: поиск по типу и региону
     * Использует native SQL вместо JPQL для работы с массивами PostgreSQL.
     */
    @Query(value = "SELECT * FROM lavka_schema.couriers WHERE courier_type = :type AND :region = ANY(regions)",
            nativeQuery = true)
    List<Courier> findByTypeAndRegion(@Param("type") String type,
                                      @Param("region") Integer region);

    // ============================================
    // Пагинация
    // ============================================

    @Query("SELECT c FROM Courier c ORDER BY c.id ASC")
    Page<Courier> findAllWithPagination(Pageable pageable);

    // ============================================
    // Статистика
    // ============================================

    @Query("SELECT DISTINCT c.courierType FROM Courier c")
    List<CourierType> findAllCourierTypes();

    @Query("SELECT COUNT(c) FROM Courier c WHERE c.courierType = :type")
    long countByType(@Param("type") CourierType type);

    @Query(value = "SELECT * FROM lavka_schema.couriers ORDER BY array_length(regions, 1) DESC LIMIT 1",
            nativeQuery = true)
    Optional<Courier> findWithMostRegions();

    // ============================================
    // Проверка существования
    // ============================================

    boolean existsByCourierType(String courierType);

    @Query(value = "SELECT COUNT(*) > 0 FROM lavka_schema.couriers WHERE id = :courierId AND :region = ANY(regions)",
            nativeQuery = true)
    boolean isCourierInRegion(@Param("courierId") Long courierId,
                              @Param("region") Integer region);
}