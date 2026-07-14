package com.yandex.lavka.repository;

import com.yandex.lavka.model.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = "courier")
    @Query(
            value = """
                    SELECT o
                    FROM Order o
                    ORDER BY o.id ASC
                    """
    )
    List<Order> findAllWithCourier();

    @EntityGraph(attributePaths = "courier")
    @Query(
            value = """
                    SELECT o
                    FROM Order o
                    ORDER BY o.id ASC
                    """
    )
    List<Order> findAllWithCourier(Pageable pageable);

    @EntityGraph(attributePaths = "courier")
    @Query(
            value = """
                    SELECT o
                    FROM Order o
                    WHERE o.id = :orderId
                    """
    )
    java.util.Optional<Order> findDetailedById(@Param("orderId") Long orderId);

    @EntityGraph(attributePaths = "courier")
    List<Order> findByCourierIdOrderByIdAsc(Long courierId);

    @EntityGraph(attributePaths = "courier")
    List<Order> findByRegionOrderByIdAsc(Integer region);

    @EntityGraph(attributePaths = "courier")
    List<Order> findByCompletedTimeIsNullOrderByIdAsc();

    @EntityGraph(attributePaths = "courier")
    List<Order> findByCompletedTimeIsNotNullOrderByIdAsc();

    @EntityGraph(attributePaths = "courier")
    @Query(
            """
            SELECT o
            FROM Order o
            WHERE o.completedTime IS NULL AND o.courier.id = :courierId
            ORDER BY o.id ASC
            """
    )
    List<Order> findIncompleteByCourierId(@Param("courierId") Long courierId);

    @EntityGraph(attributePaths = "courier")
    @Query(
            """
            SELECT o
            FROM Order o
            LEFT JOIN o.courier c
            WHERE (:region IS NULL OR o.region = :region)
              AND (:courierType IS NULL OR c.courierType = :courierType)
            ORDER BY o.id ASC
            """
    )
    List<Order> findWithFilters(
            @Param("region") Integer region,
            @Param("courierType") com.yandex.lavka.model.enums.CourierType courierType);

    @Query(
            """
            SELECT o
            FROM Order o
            WHERE o.completedTime BETWEEN :from AND :to
            ORDER BY o.completedTime ASC
            """
    )
    List<Order> findByCompletedTimeBetween(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    @Query(
            """
            SELECT COUNT(o), COALESCE(SUM(o.cost), 0)
            FROM Order o
            """
    )
    Object[] fetchOrderStatistics();
}
