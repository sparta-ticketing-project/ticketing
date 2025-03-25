package com.example.ticketing.domain.concert.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ConcertRankResponse {
    private final Long rank;
    private final Long viewCount;
    private final Long concertId;
    private final String concertName;

    @Builder
    public ConcertRankResponse(Long rank, Long viewCount, Long concertId, String concertName){
        this.rank = rank;
        this.viewCount = viewCount;
        this.concertId = concertId;
        this.concertName = concertName;
    }
}
