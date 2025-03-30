package com.example.ticketing.domain.ticket.dto.response;

import com.example.ticketing.domain.seat.enums.SeatType;
import com.example.ticketing.domain.ticket.entity.Ticket;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TicketResponse {

    private Long ticketId;
    private Long seatId;
    private Long seatDetailId;
    private SeatType seatType;
    private int seatNumber;
    private int price;

    @Builder
    private TicketResponse(
            Long ticketId,
            Long seatId,
            Long seatDetailId,
            SeatType seatType,
            int seatNumber,
            int price
    ) {
        this.ticketId = ticketId;
        this.seatId = seatId;
        this.seatDetailId = seatDetailId;
        this.seatType = seatType;
        this.seatNumber = seatNumber;
        this.price = price;
    }

    public static TicketResponse from(Ticket ticket) {
        return TicketResponse.builder()
                .ticketId(ticket.getId())
                .seatId(ticket.getSeat().getId())
                .seatDetailId(ticket.getSeat().getSeatDetail().getId())
                .seatType(ticket.getSeat().getSeatDetail().getSeatType())
                .seatNumber(ticket.getSeat().getSeatNumber())
                .price(ticket.getPrice())
                .build();
    }
}
