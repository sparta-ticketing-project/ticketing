package com.example.ticketing.domain.ticket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TicketListResponse {

    private Long ticketId;
    private Long seatId;
}
