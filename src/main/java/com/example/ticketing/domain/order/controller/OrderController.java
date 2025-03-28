package com.example.ticketing.domain.order.controller;

import com.example.ticketing.domain.order.dto.request.CreateOrderRequest;
import com.example.ticketing.domain.order.dto.response.CancelOrderResponse;
import com.example.ticketing.domain.order.dto.response.CreateOrderResponse;
import com.example.ticketing.domain.order.dto.response.OrderListResponse;
import com.example.ticketing.domain.order.dto.response.OrderResponse;
import com.example.ticketing.domain.order.enums.OrderStatus;
import com.example.ticketing.domain.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/v1/concerts/{concertId}/orders")
    public ResponseEntity<CreateOrderResponse> createOrder (
            @RequestParam Long userId,
            @PathVariable Long concertId,
            @Valid @RequestBody CreateOrderRequest createOrderRequest
    ) {
        return ResponseEntity.ok(orderService.createOrder(userId, concertId, createOrderRequest));
    }

    @GetMapping("/v1/orders/{orderId}")
    public ResponseEntity<OrderResponse> getOrder (
            @RequestParam Long userId,
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(orderService.getOrder(userId, orderId));
    }

    @GetMapping("/v1/orders")
    public ResponseEntity<Page<OrderListResponse>> getOrders (
            @RequestParam Long userId,
            @RequestParam(defaultValue = "COMPLETED") OrderStatus orderStatus,
            @PageableDefault(page = 1, size = 5) Pageable pageable
    ) {
        return ResponseEntity.ok(orderService.getOrders(userId, orderStatus, pageable));
    }

    @DeleteMapping("/v1/orders/{orderId}")
    public ResponseEntity<CancelOrderResponse> cancelOrder (
            @RequestParam Long userId,
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(orderService.cancelOrder(userId, orderId));
    }
}
