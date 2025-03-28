package com.example.ticketing.domain.seat.dto.response;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatPageResponse {
    private ConcertResponse concert;
    private List<SeatPageDataResponse> seatPageDataResponses;
}
