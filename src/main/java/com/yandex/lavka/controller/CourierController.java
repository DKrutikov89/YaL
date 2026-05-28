package com.yandex.lavka.controller;

import com.yandex.lavka.exception.CourierNotFoundException;
import com.yandex.lavka.model.dto.CourierDto;
import com.yandex.lavka.model.dto.CreateCourierRequest;
import com.yandex.lavka.model.dto.CreateCouriersResponse;
import com.yandex.lavka.service.CourierService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/couriers")
public class CourierController {

    private final CourierService courierService;

    public CourierController(CourierService courierService) {
        this.courierService = courierService;
    }

    /**
     * POST /couriers - Создание курьеров
     */
    @PostMapping
    public ResponseEntity<CreateCouriersResponse> createCouriers(
            @Valid @RequestBody CreateCourierRequest request) {

        List<CourierDto> createdCouriers = courierService.createCouriers(request.getCouriers());
        CreateCouriersResponse response = new CreateCouriersResponse(createdCouriers);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /couriers - Получение всех курьеров
     */
    @GetMapping
    public ResponseEntity<List<CourierDto>> getAllCouriers() {
        List<CourierDto> couriers = courierService.getAllCouriers();
        return ResponseEntity.ok(couriers);
    }

    /**
     * GET /couriers/{courier_id} - Получение курьера по ID
     */
    @GetMapping("/{courier_id}")
    public ResponseEntity<CourierDto> getCourierById(@PathVariable("courier_id") Long courierId) {
        CourierDto courier = courierService.getCourierById(courierId)
                .orElseThrow(() -> new CourierNotFoundException(courierId));

        return ResponseEntity.ok(courier);
    }
}