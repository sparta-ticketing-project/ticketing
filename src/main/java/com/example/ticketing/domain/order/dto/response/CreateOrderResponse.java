package com.example.ticketing.domain.order.dto.response;

import com.example.ticketing.domain.concert.entity.Concert;
import com.example.ticketing.domain.order.entity.Order;
import com.example.ticketing.domain.ticket.dto.response.TicketResponse;
import com.example.ticketing.domain.ticket.entity.Ticket;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CreateOrderResponse {

    private Long concertId;
    private Long orderId;
    private int totalPrice;
    private LocalDateTime createdAt;
    private List<TicketResponse> tickets;

    @Builder
    private CreateOrderResponse (
            Long concertId,
            Long orderId,
            int totalPrice,
            LocalDateTime createdAt,
            List<TicketResponse> tickets
    ) {
        this.concertId = concertId;
        this.orderId = orderId;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
        this.tickets = tickets;
    }

    public static CreateOrderResponse of(Concert concert, Order order, List<Ticket> tickets) {
        return CreateOrderResponse.builder()
                .concertId(concert.getId())
                .orderId(order.getId())
                .totalPrice(order.getTotalPrice())
                .createdAt(order.getCreatedAt())
                .tickets(tickets.stream().map(TicketResponse::from).toList())
                .build();
    }
}
