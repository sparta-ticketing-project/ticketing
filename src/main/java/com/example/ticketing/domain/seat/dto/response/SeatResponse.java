package com.example.ticketing.domain.seat.dto.response;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatResponse {
    private ConcertResponse concert;
    private List<SeatDetailResponse> seatDetails;
    private SeatPageResponse seats;
}
