package com.example.ticketing.domain.concert.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ConcertSingleResponse {
    private final Long id;
    private final String concertName;
    private final String concertType;
    private final LocalDateTime concertDate;
    private final LocalDateTime ticketingDate;
    private final Integer totalSeatCount;
    private final Integer availableSeatCount;
    private final Long viewCount;
    private final List<ConcertSeatDetailResponse> concertSeatDetailResponses;

    @Builder
    public ConcertSingleResponse(Long id, String concertName, String concertType, LocalDateTime concertDate, LocalDateTime ticketingDate, Integer totalSeatCount, Integer availableSeatCount, Long viewCount, List<ConcertSeatDetailResponse> concertSeatDetailResponses) {
        this.id = id;
        this.concertName = concertName;
        this.concertType = concertType;
        this.concertDate = concertDate;
        this.ticketingDate = ticketingDate;
        this.totalSeatCount = totalSeatCount;
        this.availableSeatCount = availableSeatCount;
        this.viewCount = viewCount;
        this.concertSeatDetailResponses = concertSeatDetailResponses;
    }
}
