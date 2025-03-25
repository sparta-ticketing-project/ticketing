package com.example.ticketing.domain.concert.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ConcertSeatDetailResponse {
    private final String seatType;
    private final Integer price;
    private final Integer totalSeatCount;
    private final Integer availableSeatCount;

    @Builder
    public ConcertSeatDetailResponse(String seatType, Integer price, Integer totalSeatCount, Integer availableSeatCount) {
        this.seatType = seatType;
        this.price = price;
        this.totalSeatCount = totalSeatCount;
        this.availableSeatCount = availableSeatCount;
    }
}
