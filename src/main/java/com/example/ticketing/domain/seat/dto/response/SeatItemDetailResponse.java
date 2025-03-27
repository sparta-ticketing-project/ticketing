package com.example.ticketing.domain.seat.dto.response;

import com.example.ticketing.domain.seat.enums.SeatType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatItemDetailResponse {
    private Long seatId;
    private Long concertId;
    private Long seatDetailId;
    private SeatType seatType;
    private int price;
    private boolean isAvailable;
    private int seatNumber;
}
