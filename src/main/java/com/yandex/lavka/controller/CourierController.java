package com.yandex.lavka.controller;

import com.yandex.lavka.exception.CourierNotFoundException;
import com.yandex.lavka.model.dto.CourierDto;
import com.yandex.lavka.model.dto.CreateCourierRequest;
import com.yandex.lavka.model.dto.CreateCouriersResponse;
import com.yandex.lavka.ratelimit.RateLimit;
import com.yandex.lavka.ratelimit.RateLimitKeyType;
import com.yandex.lavka.service.CourierService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/couriers")
public class CourierController {

    private static final Logger logger = LoggerFactory.getLogger(CourierController.class);

    private final CourierService courierService;

    public CourierController(CourierService courierService) {
        this.courierService = courierService;
        logger.info("CourierController initialized");
    }

    @PostMapping
    @RateLimit(requestsPerSecond = 5, burstCapacity = 10, keyType = RateLimitKeyType.IP_AND_PATH)
    public ResponseEntity<CreateCouriersResponse> createCouriers(@Valid @RequestBody CreateCourierRequest request) {
        logger.info("Received POST /couriers with {} couriers", request.getCouriers().size());

        List<CourierDto> createdCouriers = courierService.createCouriers(request.getCouriers());
        return ResponseEntity.ok(new CreateCouriersResponse(createdCouriers));
    }

    @GetMapping
    @RateLimit(requestsPerSecond = 10, burstCapacity = 10, keyType = RateLimitKeyType.IP_AND_PATH)
    public ResponseEntity<List<CourierDto>> getAllCouriers(
            @RequestParam(required = false) @Min(1) Integer limit,
            @RequestParam(required = false) @Min(0) Integer offset) {
        logger.info("Received GET /couriers with limit={} and offset={}", limit, offset);

        if (limit == null && offset == null) {
            return ResponseEntity.ok(courierService.getAllCouriers());
        }

        return ResponseEntity.ok(courierService.getCouriersWithPagination(
                limit != null ? limit : 10,
                offset != null ? offset : 0));
    }

    @GetMapping("/{courier_id}")
    @RateLimit(requestsPerSecond = 10, burstCapacity = 10, keyType = RateLimitKeyType.IP_AND_PATH)
    public ResponseEntity<CourierDto> getCourierById(@PathVariable("courier_id") Long courierId) {
        logger.info("Received GET /couriers/{}", courierId);

        CourierDto courier = courierService.getCourierById(courierId)
                .orElseThrow(() -> new CourierNotFoundException(courierId));

        return ResponseEntity.ok(courier);
    }
}
