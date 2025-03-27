package com.example.ticketing.domain.order.dto.response;

import com.example.ticketing.domain.concert.entity.Concert;
import com.example.ticketing.domain.order.dto.request.CreateOrderRequest;
import com.example.ticketing.domain.order.entity.Order;
import com.example.ticketing.domain.order.enums.OrderStatus;
import com.example.ticketing.domain.ticket.dto.response.TicketResponse;
import com.example.ticketing.domain.ticket.entity.Ticket;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderResponse {

    private Long orderId;
    private LocalDateTime createdAt;
    private OrderStatus orderStatus;
    private Long concertId;
    private String concertName;
    private LocalDateTime concertDate;
    private List<TicketResponse> tickets;
    private int totalPrice;

    @Builder
    public OrderResponse(
            Long orderId,
            LocalDateTime createdAt,
            OrderStatus orderStatus,
            Long concertId,
            String concertName,
            LocalDateTime concertDate,
            List<TicketResponse> tickets,
            int totalPrice
    ) {
        this.orderId = orderId;
        this.createdAt = createdAt;
        this.orderStatus = orderStatus;
        this.concertId = concertId;
        this.concertName = concertName;
        this.concertDate = concertDate;
        this.tickets = tickets;
        this.totalPrice = totalPrice;
    }

    public static OrderResponse of(Concert concert, Order order, List<Ticket> tickets) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .createdAt(order.getCreatedAt())
                .orderStatus(order.getOrderStatus())
                .concertId(concert.getId())
                .concertName(concert.getConcertName())
                .concertDate(concert.getConcertDate())
                .tickets(tickets.stream().map(TicketResponse::from).toList())
                .totalPrice(order.getTotalPrice())
                .build();
    }
}
