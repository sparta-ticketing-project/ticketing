package com.example.ticketing.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeatResponse {
    private Long id;
    private int seatNumber;
    private boolean isAvailable;
}
