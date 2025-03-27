package com.example.ticketing.domain.seat.dto.response;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatItemResponse {
    private Long seatId;
    private int seatNumber;
    private Long seatDetailId;
}
