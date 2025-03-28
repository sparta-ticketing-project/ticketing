package com.example.ticketing.domain.seat.dto.response;

import com.example.ticketing.domain.seat.enums.SeatType;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatPageDataResponse {
    private Long seatId;
    private boolean isAvailable;
    private int seatNumber;
    private SeatType seatType;
    private int price;
}
