package com.yandex.lavka.repository;

import com.yandex.lavka.model.entity.Courier;
import com.yandex.lavka.model.enums.CourierType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourierRepository extends JpaRepository<Courier, Long> {

    List<Courier> findByCourierTypeOrderByIdAsc(CourierType courierType);

    long countByCourierType(CourierType courierType);

    @Query(
            value = """
                    SELECT *
                    FROM lavka_schema.couriers
                    ORDER BY id ASC
                    LIMIT :limit OFFSET :offset
                    """,
            nativeQuery = true
    )
    List<Courier> findAllWithLimitOffset(@Param("limit") int limit, @Param("offset") int offset);

    @Query(
            value = """
                    SELECT *
                    FROM lavka_schema.couriers
                    WHERE courier_type = :courierType
                    ORDER BY id ASC
                    LIMIT :limit OFFSET :offset
                    """,
            nativeQuery = true
    )
    List<Courier> findByCourierType(
            @Param("courierType") String courierType,
            @Param("limit") int limit,
            @Param("offset") int offset);

    @Query(
            value = """
                    SELECT *
                    FROM lavka_schema.couriers
                    WHERE :region = ANY(regions)
                    ORDER BY id ASC
                    """,
            nativeQuery = true
    )
    List<Courier> findByRegionContaining(@Param("region") Integer region);

    @Query(
            value = """
                    SELECT *
                    FROM lavka_schema.couriers
                    WHERE :region = ANY(regions)
                    ORDER BY id ASC
                    LIMIT :limit OFFSET :offset
                    """,
            nativeQuery = true
    )
    List<Courier> findByRegionContaining(
            @Param("region") Integer region,
            @Param("limit") int limit,
            @Param("offset") int offset);
}
