package com.yandex.lavka.controller;

import com.yandex.lavka.model.dto.CourierMetaInfoResponse;
import com.yandex.lavka.ratelimit.RateLimit;
import com.yandex.lavka.ratelimit.RateLimitKeyType;
import com.yandex.lavka.service.RatingService;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Validated
@RestController
@RequestMapping("/couriers/meta-info")
public class AnalyticsController {

    private final RatingService ratingService;

    public AnalyticsController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping("/{courier_id}")
    @RateLimit(requestsPerSecond = 10, burstCapacity = 10, keyType = RateLimitKeyType.IP_AND_PATH)
    public ResponseEntity<CourierMetaInfoResponse> getCourierMetaInfo(
            @PathVariable("courier_id") @Positive Long courierId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(ratingService.getCourierMetaInfo(courierId, startDate, endDate));
    }
}
