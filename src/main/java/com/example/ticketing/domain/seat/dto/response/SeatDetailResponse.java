package com.example.ticketing.domain.seat.dto.response;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatDetailResponse {
    private Long id;
    private String seatType;
    private int price;
}
