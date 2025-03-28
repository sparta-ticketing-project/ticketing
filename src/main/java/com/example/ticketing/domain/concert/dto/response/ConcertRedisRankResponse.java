package com.example.ticketing.domain.concert.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;

@Getter
public class ConcertRedisRankResponse {
    private final Integer rank;
    private final String concertName;
    private final Long concertId;
    private final Long viewCount;

    @Builder
    public ConcertRedisRankResponse(Integer rank, String concertName, Long concertId, Long viewCount) {
        this.rank = rank;
        this.concertName = concertName;
        this.concertId = concertId;
        this.viewCount = viewCount;
    }

}
