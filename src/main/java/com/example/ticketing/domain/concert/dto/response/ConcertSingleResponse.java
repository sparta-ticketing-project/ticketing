package com.example.ticketing.domain.concert.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ConcertSingleResponse {
    private Long id;
    private String concertName;
    private String concertType;
    private LocalDateTime concertDate;
    private LocalDateTime ticketingDate;
    private Integer totalSeatCount;
    private Integer availableSeatCount;
    private List<ConcertSeatDetailResponse> concertSeatDetailResponses;

    @Builder
    public ConcertSingleResponse(Long id, String concertName, String concertType, LocalDateTime concertDate, LocalDateTime ticketingDate, Integer totalSeatCount, Integer availableSeatCount, List<ConcertSeatDetailResponse> concertSeatDetailResponses) {
        this.id = id;
        this.concertName = concertName;
        this.concertType = concertType;
        this.concertDate = concertDate;
        this.ticketingDate = ticketingDate;
        this.totalSeatCount = totalSeatCount;
        this.availableSeatCount = availableSeatCount;
        this.concertSeatDetailResponses = concertSeatDetailResponses;
    }
}
