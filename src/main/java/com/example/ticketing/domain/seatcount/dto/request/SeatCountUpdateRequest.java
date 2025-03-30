package com.example.ticketing.domain.seatcount.dto.request;

import com.example.ticketing.domain.order.dto.response.CancelOrderResponse;
import com.example.ticketing.domain.order.dto.response.CreateOrderResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SeatCountUpdateRequest {

    private final ConcertSeatCountChange concertSeatCountChange;
    private final SeatDetailCountChange seatDetailCountChange;

    @Builder
    private SeatCountUpdateRequest(
            ConcertSeatCountChange concertSeatCountChange,
            SeatDetailCountChange seatDetailCountChange
    ) {
        this.concertSeatCountChange = concertSeatCountChange;
        this.seatDetailCountChange = seatDetailCountChange;
    }

    public static SeatCountUpdateRequest forDecrement(CreateOrderResponse response) {
        return SeatCountUpdateRequest.builder()
                .concertSeatCountChange(
                        ConcertSeatCountChange.forDecrement(response.getConcertId(), response.getTickets().size())
                )
                .seatDetailCountChange(
                        SeatDetailCountChange.forDecrement(response.getTickets())
                )
                .build();
    }

    public static SeatCountUpdateRequest forIncrement(CancelOrderResponse response) {
        return SeatCountUpdateRequest.builder()
                .concertSeatCountChange(
                        ConcertSeatCountChange.forIncrement(response.getConcertId(), response.getTickets().size())
                )
                .seatDetailCountChange(
                        SeatDetailCountChange.forIncrement(response.getTickets())
                )
                .build();
    }
}
