package com.yandex.lavka.service;

import com.yandex.lavka.exception.CourierNotFoundException;
import com.yandex.lavka.model.dto.CourierCoefficients;
import com.yandex.lavka.model.dto.CourierMetaInfoResponse;
import com.yandex.lavka.model.dto.CourierStatistics;
import com.yandex.lavka.model.entity.Courier;
import com.yandex.lavka.repository.CourierRepository;
import com.yandex.lavka.repository.EarningsProjection;
import com.yandex.lavka.repository.OrderRepository;
import com.yandex.lavka.repository.RatingProjection;
import com.yandex.lavka.util.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Service
public class RatingService {

    private static final Logger logger = LoggerFactory.getLogger(RatingService.class);
    private static final String META_CACHE = "courierMetaInfo";

    private final CourierRepository courierRepository;
    private final OrderRepository orderRepository;
    private final TimeUtils timeUtils;

    public RatingService(
            CourierRepository courierRepository,
            OrderRepository orderRepository,
            TimeUtils timeUtils) {
        this.courierRepository = courierRepository;
        this.orderRepository = orderRepository;
        this.timeUtils = timeUtils;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = META_CACHE, key = "#courierId + ':' + #startDate + ':' + #endDate")
    public CourierMetaInfoResponse getCourierMetaInfo(Long courierId, LocalDate startDate, LocalDate endDate) {
        timeUtils.validateDateRange(startDate, endDate);

        Courier courier = courierRepository.findById(courierId)
                .orElseThrow(() -> new CourierNotFoundException(courierId));

        CourierStatistics statistics = getCourierStatistics(courier, startDate, endDate);

        logger.debug("Calculated meta info for courierId={}, period={}..{}", courierId, startDate, endDate);

        return CourierMetaInfoResponse.builder()
                .courierId(courier.getId())
                .courierType(courier.getCourierType())
                .regions(courier.getRegions())
                .workingHours(courier.getWorkingHours())
                .rating(statistics.getRating())
                .earnings(statistics.getEarnings())
                .build();
    }

    @Transactional(readOnly = true)
    public CourierStatistics getCourierStatistics(Courier courier, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        Optional<EarningsProjection> earningsProjection =
                orderRepository.findEarningsProjection(courier.getId(), startDateTime, endDateTime);
        Optional<RatingProjection> ratingProjection =
                orderRepository.findRatingProjection(courier.getId(), startDateTime, endDateTime);

        long completedOrders = ratingProjection.map(RatingProjection::getCompletedOrders).orElse(0L);
        Long totalCost = earningsProjection.map(EarningsProjection::getTotalCost).orElse(0L);

        if (completedOrders == 0L) {
            return CourierStatistics.builder()
                    .courierId(courier.getId())
                    .completedOrders(0L)
                    .totalCost(0L)
                    .earnings(null)
                    .rating(null)
                    .averageOrderCost(null)
                    .productivity(null)
                    .build();
        }

        long earnings = totalCost * CourierCoefficients.earningsCoefficient(courier.getCourierType());
        BigDecimal totalWorkingHours = timeUtils.getTotalWorkingHours(courier.getWorkingHours(), startDate, endDate);
        BigDecimal rating = calculateRating(completedOrders, totalWorkingHours, courier);
        BigDecimal averageOrderCost = BigDecimal.valueOf(totalCost)
                .divide(BigDecimal.valueOf(completedOrders), 2, RoundingMode.HALF_UP);
        BigDecimal productivity = BigDecimal.valueOf(completedOrders)
                .divide(totalWorkingHours, 4, RoundingMode.HALF_UP);

        return CourierStatistics.builder()
                .courierId(courier.getId())
                .completedOrders(completedOrders)
                .totalCost(totalCost)
                .earnings(earnings)
                .rating(rating)
                .averageOrderCost(averageOrderCost)
                .productivity(productivity)
                .build();
    }

    private BigDecimal calculateRating(long completedOrders, BigDecimal totalWorkingHours, Courier courier) {
        if (totalWorkingHours.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        return BigDecimal.valueOf(completedOrders)
                .divide(totalWorkingHours, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(CourierCoefficients.ratingCoefficient(courier.getCourierType())))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
