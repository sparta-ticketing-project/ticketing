package com.example.ticketing.domain.order.service;

import com.example.ticketing.domain.order.dto.request.CreateOrderRequest;
import com.example.ticketing.domain.order.dto.response.CancelOrderResponse;
import com.example.ticketing.domain.order.dto.response.CreateOrderResponse;
import com.example.ticketing.domain.seatcount.dto.request.SeatCountUpdateRequest;
import com.example.ticketing.domain.seatcount.service.SeatCountCacheService;
import com.example.ticketing.global.lock.service.LockService;
import com.example.ticketing.global.util.RedisKeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OrderLockFacade {

    private final LockService lockService;
    private final OrderService orderService;
    private final SeatCountCacheService seatCountCacheService;

    @Transactional
    public CreateOrderResponse createOrder(Long userId, Long concertId, CreateOrderRequest request) {
        Map<String, String> locks = new HashMap<>();

        try {
            List<String> seatLockKeys = request.getSeatIds().stream()
                    .map(seatId -> RedisKeyGenerator.seatLock(seatId))
                    .toList();
            locks.putAll(lockService.acquireLocks(seatLockKeys));

            String userLockKey = RedisKeyGenerator.userLock(userId);
            locks.putAll(lockService.acquireLock(userLockKey));

            CreateOrderResponse response = orderService.createOrder(userId, concertId, request);

            seatCountCacheService.updateSeatCount(SeatCountUpdateRequest.forDecrement(response));

            return response;
        } finally {
            lockService.releaseLocks(locks);
        }
    }

    @Transactional
    public CancelOrderResponse cancelOrder(Long userId, Long orderId) {
        Map<String, String> locks = new HashMap<>();

        try {
            String orderLockKey = RedisKeyGenerator.orderLock(orderId);
            locks.putAll(lockService.acquireLock(orderLockKey));
            String userLockKey = RedisKeyGenerator.userLock(userId);
            locks.putAll(lockService.acquireLock(userLockKey));

            CancelOrderResponse response = orderService.cancelOrder(userId, orderId);

            seatCountCacheService.updateSeatCount(SeatCountUpdateRequest.forIncrement(response));

            return response;
        } finally {
            lockService.releaseLocks(locks);
        }
    }
}
