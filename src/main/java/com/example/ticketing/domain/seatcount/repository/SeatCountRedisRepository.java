package com.example.ticketing.domain.seatcount.repository;

import com.example.ticketing.global.util.RedisKeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class SeatCountRedisRepository {

    private final StringRedisTemplate redisTemplate;

    public void incrementSeatDetailCount(Long seatDetailId, int amount) {
        String key = RedisKeyGenerator.seatDetailSeatCount(seatDetailId);
        redisTemplate.opsForValue().increment(key, amount);
    }

    public void incrementConcertCount(Long concertId, int amount) {
        String key = RedisKeyGenerator.concertSeatCount(concertId);
        redisTemplate.opsForValue().increment(key, amount);
    }

    public Long getSeatDetailSeatCount(Long seatDetailId) {
        String key = RedisKeyGenerator.seatDetailSeatCount(seatDetailId);
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value) : 0L;
    }

    public Long getConcertSeatCount(Long concertId) {
        String key = RedisKeyGenerator.concertSeatCount(concertId);
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value) : 0L;
    }

    public void resetSeatDetailSeatCount(Long seatDetailId) {
        redisTemplate.delete(RedisKeyGenerator.seatDetailSeatCount(seatDetailId));
    }

    public void resetConcertSeatCount(Long concertId) {
        redisTemplate.delete(RedisKeyGenerator.concertSeatCount(concertId));
    }

    public Set<String> findSeatDetailSeatCountKeys() {
        return redisTemplate.keys(RedisKeyGenerator.SEAT_DETAIL_COUNT_PREFIX + "*");
    }

    public Set<String> findConcertSeatCountKeys() {
        return redisTemplate.keys(RedisKeyGenerator.CONCERT_COUNT_PREFIX + "*");
    }
}
