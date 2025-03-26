package com.example.ticketing.domain.order.controller;

import com.example.ticketing.domain.order.dto.request.CreateOrderRequest;
import com.example.ticketing.domain.order.dto.response.CreateOrderResponse;
import com.example.ticketing.domain.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

}
