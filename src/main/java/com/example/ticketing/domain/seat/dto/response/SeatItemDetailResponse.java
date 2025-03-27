package com.example.ticketing.domain.seat.dto.response;

import com.example.ticketing.domain.seat.enums.SeatType;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatItemDetailResponse {
    private Long seatId;
    private Long concertId;
    private Long seatDetailId;
    private SeatType seatType;
    private int price;
    private boolean isAvailable;
    private int seatNumber;
}
