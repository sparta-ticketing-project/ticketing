package com.example.ticketing.domain.order.dto.response;

import com.example.ticketing.domain.order.entity.Order;
import com.example.ticketing.domain.order.enums.OrderStatus;
import com.example.ticketing.domain.ticket.dto.response.TicketResponse;
import com.example.ticketing.domain.ticket.entity.Ticket;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CancelOrderResponse {

    private Long orderId;
    private OrderStatus orderStatus;
    private Long concertId;
    private int totalPrice;
    private List<TicketResponse> tickets;

    @Builder
    private CancelOrderResponse (
            Long orderId,
            OrderStatus orderStatus,
            Long concertId,
            int totalPrice,
            List<TicketResponse> tickets
    ) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.concertId = concertId;
        this.totalPrice = totalPrice;
        this.tickets = tickets;
    }

    public static CancelOrderResponse of(Order order, List<Ticket> tickets) {
        return CancelOrderResponse.builder()
                .orderId(order.getId())
                .orderStatus(order.getOrderStatus())
                .concertId(order.getConcert().getId())
                .totalPrice(order.getTotalPrice())
                .tickets(tickets.stream().map(TicketResponse::from).toList())
                .build();
    }
}
