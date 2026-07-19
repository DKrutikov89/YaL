package com.yandex.lavka.service;

import com.yandex.lavka.exception.CourierNotFoundException;
import com.yandex.lavka.exception.CourierOrderMismatchException;
import com.yandex.lavka.exception.InvalidOrderStateException;
import com.yandex.lavka.exception.OrderNotFoundException;
import com.yandex.lavka.model.dto.CompleteOrderDto;
import com.yandex.lavka.model.dto.CreateOrderDto;
import com.yandex.lavka.model.dto.OrderDto;
import com.yandex.lavka.model.entity.Courier;
import com.yandex.lavka.model.entity.Order;
import com.yandex.lavka.repository.CourierRepository;
import com.yandex.lavka.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;

    public OrderService(OrderRepository orderRepository, CourierRepository courierRepository) {
        this.orderRepository = orderRepository;
        this.courierRepository = courierRepository;
    }

    @Transactional
    public List<OrderDto> createOrders(List<CreateOrderDto> createOrderDtos) {
        logger.info("Creating {} orders", createOrderDtos.size());

        List<Order> orders = createOrderDtos.stream()
                .map(this::toEntity)
                .toList();

        return orderRepository.saveAll(orders).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getAllOrders() {
        logger.debug("Fetching all orders");
        return orderRepository.findAllWithCourier().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersWithPagination(int limit, int offset) {
        logger.debug("Fetching orders with limit={} and offset={}", limit, offset);
        Pageable pageable = PageRequest.of(0, offset + limit);
        return orderRepository.findAllWithCourier(pageable).stream()
                .skip(offset)
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<OrderDto> getOrderById(Long orderId) {
        logger.debug("Fetching order by id={}", orderId);
        return orderRepository.findDetailedById(orderId).map(this::toDto);
    }

    @Transactional
    public List<OrderDto> completeOrders(List<CompleteOrderDto> completeOrderDtos) {
        logger.info("Completing {} orders", completeOrderDtos.size());

        return completeOrderDtos.stream()
                .map(this::completeSingleOrder)
                .toList();
    }

    private OrderDto completeSingleOrder(CompleteOrderDto completeOrderDto) {
        Order order = orderRepository.findDetailedById(completeOrderDto.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(completeOrderDto.getOrderId()));

        Courier courier = courierRepository.findById(completeOrderDto.getCourierId())
                .orElseThrow(() -> new CourierNotFoundException(completeOrderDto.getCourierId()));

        if (order.getCourier() != null && !order.getCourier().getId().equals(courier.getId())) {
            throw new CourierOrderMismatchException(courier.getId(), order.getId());
        }

        if (order.getCompletedTime() != null) {
            if (order.getCourier() != null
                    && order.getCourier().getId().equals(courier.getId())
                    && order.getCompletedTime().equals(completeOrderDto.getCompleteTime())) {
                return toDto(order);
            }
            throw new InvalidOrderStateException(order.getId(), "error.order.already-completed");
        }

        order.setCourier(courier);
        order.setCompletedTime(completeOrderDto.getCompleteTime());

        return toDto(orderRepository.save(order));
    }

    private Order toEntity(CreateOrderDto dto) {
        Order order = new Order();
        order.setWeight(dto.getWeight());
        order.setRegion(dto.getRegion());
        order.setDeliveryHours(dto.getDeliveryHours());
        order.setCost(dto.getCost());
        return order;
    }

    private OrderDto toDto(Order order) {
        return new OrderDto(
                order.getId(),
                order.getWeight(),
                order.getRegion(),
                order.getDeliveryHours(),
                order.getCost(),
                order.getCourier() != null ? order.getCourier().getId() : null,
                order.getCompletedTime()
        );
    }
}
