package com.example.ticketing.domain.seat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatItemResponse {
    private Long seatId;
    private int seatNumber;
    private Long seatDetailId;
}
