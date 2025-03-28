package com.example.ticketing.domain.concert.repository;

import com.example.ticketing.domain.concert.dto.response.ConcertRedisRankResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ConcertRedisRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String RANKING = "ranking";

    public void incrementViewCount(Long concertId){
        redisTemplate.opsForZSet().incrementScore(RANKING, String.valueOf(concertId), 1);
    }

    public void cacheConcertName(Long concertId, String concertName){

        // 자정까지 남은 시간 계산
        Duration ttl = getDuration();

        redisTemplate.opsForValue().set(concertId.toString(), concertName, ttl);
    }


    public boolean isFirstView(Long userId, Long concertId){
        String key = userId + ":" + concertId;

        // 자정까지 남은 시간 계산
        Duration ttl = getDuration();

        Boolean isNew = redisTemplate.opsForValue().setIfAbsent(key, "1", ttl);

        return Boolean.TRUE.equals(isNew);
    }

    public List<ConcertRedisRankResponse> getRankList(int rankRange){
        Set<ZSetOperations.TypedTuple<Object>> typedTuples = redisTemplate.opsForZSet().reverseRangeWithScores(RANKING, 0, rankRange - 1);

        if(typedTuples == null){
            return List.of();
        }

        List<ConcertRedisRankResponse> concertRedisRankResponses = new ArrayList<>();

        int rankCount = 1;
        for(ZSetOperations.TypedTuple<Object> tuple : typedTuples){

            String concertName = redisTemplate.opsForValue().get(tuple.getValue()).toString();

            ConcertRedisRankResponse concertRedisRankResponse = ConcertRedisRankResponse.builder()
                    .rank(rankCount++)
                    .concertName(concertName)
                    .concertId(Long.parseLong(tuple.getValue().toString()))
                    .viewCount(tuple.getScore().longValue())
                    .build();

            concertRedisRankResponses.add(concertRedisRankResponse);

        }

        return concertRedisRankResponses;
    }

    @Scheduled(cron = "0 0 0 * * *")
    //@Scheduled(fixedRate = 60000)
    public void resetViewRanking(){
        redisTemplate.delete(RANKING);
    }

    private Duration getDuration() {
        Duration ttl = Duration.between(LocalDateTime.now(), LocalDate.now().plusDays(1).atStartOfDay());
        return ttl;
    }
}
