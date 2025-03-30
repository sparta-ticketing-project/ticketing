package com.example.ticketing.domain.seatcount.dto.request;

import lombok.Getter;

@Getter
public class ConcertSeatCountChange {

    private final Long concertId;
    private final int count;

    private ConcertSeatCountChange(Long concertId, int count) {
        this.concertId = concertId;
        this.count = count;
    }

    public static ConcertSeatCountChange forDecrement(Long concertId, int count) {
        return new ConcertSeatCountChange(concertId, -count);
    }

    public static ConcertSeatCountChange forIncrement(Long concertId, int count) {
        return new ConcertSeatCountChange(concertId, count);
    }
}
