package com.yandex.lavka.controller;

import com.yandex.lavka.exception.OrderNotFoundException;
import com.yandex.lavka.model.dto.CompleteOrderRequest;
import com.yandex.lavka.model.dto.CompleteOrdersResponse;
import com.yandex.lavka.model.dto.CreateOrderRequest;
import com.yandex.lavka.model.dto.CreateOrdersResponse;
import com.yandex.lavka.model.dto.OrderDto;
import com.yandex.lavka.service.OrderService;
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
@RequestMapping("/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<CreateOrdersResponse> createOrders(@Valid @RequestBody CreateOrderRequest request) {
        logger.info("Received POST /orders with {} orders", request.getOrders().size());
        return ResponseEntity.ok(new CreateOrdersResponse(orderService.createOrders(request.getOrders())));
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getOrders(
            @RequestParam(required = false) @Min(1) Integer limit,
            @RequestParam(required = false) @Min(0) Integer offset) {
        logger.info("Received GET /orders with limit={} and offset={}", limit, offset);

        if (limit == null && offset == null) {
            return ResponseEntity.ok(orderService.getAllOrders());
        }

        return ResponseEntity.ok(orderService.getOrdersWithPagination(
                limit != null ? limit : 10,
                offset != null ? offset : 0));
    }

    @GetMapping("/{order_id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable("order_id") Long orderId) {
        logger.info("Received GET /orders/{}", orderId);
        OrderDto order = orderService.getOrderById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return ResponseEntity.ok(order);
    }

    @PostMapping("/complete")
    public ResponseEntity<CompleteOrdersResponse> completeOrders(@Valid @RequestBody CompleteOrderRequest request) {
        logger.info("Received POST /orders/complete with {} completion records", request.getCompleteInfo().size());
        return ResponseEntity.ok(new CompleteOrdersResponse(orderService.completeOrders(request.getCompleteInfo())));
    }
}
