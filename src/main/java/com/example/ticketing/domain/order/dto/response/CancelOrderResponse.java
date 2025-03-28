package com.example.ticketing.domain.order.dto.response;

import com.example.ticketing.domain.order.entity.Order;
import com.example.ticketing.domain.order.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CancelOrderResponse {

    private Long orderId;
    private OrderStatus orderStatus;
    private Long concertId;
    private int totalPrice;
    private LocalDateTime modifiedAt;

    @Builder
    private CancelOrderResponse (
            Long orderId,
            OrderStatus orderStatus,
            Long concertId,
            int totalPrice,
            LocalDateTime modifiedAt
            ) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.concertId = concertId;
        this.totalPrice = totalPrice;
        this.modifiedAt = modifiedAt;
    }

    public static CancelOrderResponse from(Order order) {
        return CancelOrderResponse.builder()
                .orderId(order.getId())
                .orderStatus(order.getOrderStatus())
                .concertId(order.getConcert().getId())
                .totalPrice(order.getTotalPrice())
                .modifiedAt(order.getModifiedAt())
                .build();
    }
}
