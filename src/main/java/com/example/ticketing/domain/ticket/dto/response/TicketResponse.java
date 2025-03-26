package com.example.ticketing.domain.ticket.dto.response;

import com.example.ticketing.domain.seat.enums.SeatType;
import com.example.ticketing.domain.ticket.entity.Ticket;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TicketResponse {

    private Long ticketId;
    private Long seatId;
    private SeatType seatType;
    private int seatNumber;
    private int price;

    @Builder
    private TicketResponse(Long ticketId, Long seatId, SeatType seatType, int seatNumber, int price) {
        this.ticketId = ticketId;
        this.seatId = seatId;
        this.seatType = seatType;
        this.seatNumber = seatNumber;
        this.price = price;
    }

    public static TicketResponse from(Ticket ticket) {
        return TicketResponse.builder()
                .ticketId(ticket.getId())
                .seatId(ticket.getSeat().getId())
                .seatType(ticket.getSeat().getSeatDetail().getSeatType())
                .seatNumber(ticket.getSeat().getSeatNumber())
                .price(ticket.getPrice())
                .build();
    }
}
