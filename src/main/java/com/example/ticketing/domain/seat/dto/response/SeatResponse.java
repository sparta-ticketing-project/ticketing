package com.example.ticketing.domain.seat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatResponse {
    private ConcertResponse concert;
    private List<SeatDetailResponse> seatDetails;
    private SeatPageResponse seats;
}
