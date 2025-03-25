package com.example.ticketing.domain.concert.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;

@Getter
public class ConcertRedisRankResponse {
    private Integer rank;
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

    public static ConcertRedisRankResponse of(ZSetOperations.TypedTuple<Object> tuple, Integer rank){

        String str[] = tuple.getValue().toString().split(":");

        return ConcertRedisRankResponse.builder()
                .rank(rank)
                .concertName(str[1])
                .concertId(Long.parseLong(str[0]))
                .viewCount(tuple.getScore().longValue())
                .build();
    }
}
