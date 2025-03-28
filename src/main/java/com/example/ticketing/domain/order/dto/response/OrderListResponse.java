package com.example.ticketing.domain.order.dto.response;

import com.example.ticketing.domain.order.enums.OrderStatus;
import com.example.ticketing.domain.ticket.dto.response.TicketListResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class OrderListResponse {

    private Long orderId;
    private LocalDateTime createdAt;
    private OrderStatus orderStatus;
    private Long concertId;
    private String concertName;
    private LocalDateTime concertDate;
    private List<TicketListResponse> tickets;
    private int totalPrice;
}
