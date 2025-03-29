package com.example.ticketing.domain.concert.repository;

import com.example.ticketing.domain.concert.dto.response.ConcertRedisRankResponse;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
@SpringBootTest
class ConcertRedisRepositoryTest {
    @Autowired
    ConcertRedisRepository concertRedisRepository;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void setup(){
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @Test
    @DisplayName("조회수 증가 테스트")
    void incrementViewCountTest(){
        // given
        Long concertId = 1001L;

        // when
        concertRedisRepository.incrementViewCount(concertId);

        // then
        Double score = redisTemplate.opsForZSet().score("ranking", concertId.toString());

        assertThat(score).isEqualTo(1.0);
    }

    @Test
    @DisplayName("콘서트 이름 캐싱 테스트")
    void cacheConcertNameTest(){
        // given
        Long concertId = 1L;
        String concertName = "concertName";

        // when
        concertRedisRepository.cacheConcertName(concertId, concertName);

        // then
        Object value = redisTemplate.opsForValue().get(concertId.toString());

        assertThat(value).isEqualTo(concertName);
    }

    @Test
    @DisplayName("콘서트 이름 수정 테스트")
    void setConcertNameTest(){
        // given
        Long concertId = 1L;
        String concertName = "concertName";
        String updateName = "updateName";

        // when
        concertRedisRepository.cacheConcertName(concertId, concertName);

        concertRedisRepository.setConcertName(concertId, updateName);

        // then
        Object value = redisTemplate.opsForValue().get(concertId.toString());

        assertThat(value).isEqualTo(updateName);
    }

    @Test
    @DisplayName("처음 조회 여부 확인 테스트")
    void isFirstViewTest(){
        // given
        Long userId = 1L;
        Long concertId = 1000L;

        // when
        boolean first = concertRedisRepository.isFirstView(userId, concertId);
        boolean second = concertRedisRepository.isFirstView(userId, concertId);

        // then
        assertThat(first).isTrue();
        assertThat(second).isFalse();
    }

    @Test
    @DisplayName("랭킹 리스트 조회 테스트")
    void getRankListTest(){
        // given
        concertRedisRepository.incrementViewCount(1L);
        concertRedisRepository.incrementViewCount(2L);
        concertRedisRepository.incrementViewCount(2L);

        concertRedisRepository.cacheConcertName(1L, "콘서트 A");
        concertRedisRepository.cacheConcertName(2L, "콘서트 B");

        // when
        List<ConcertRedisRankResponse> rankList = concertRedisRepository.getRankList(10);

        // then
        assertThat(rankList).hasSize(2);
        assertThat(rankList.get(0).getConcertId()).isEqualTo(2);
        assertThat(rankList.get(0).getRank()).isEqualTo(1);
        assertThat(rankList.get(0).getConcertName()).isEqualTo("콘서트 B");
        assertThat(rankList.get(0).getViewCount()).isEqualTo(2);

        assertThat(rankList.get(1).getConcertId()).isEqualTo(1);
        assertThat(rankList.get(1).getRank()).isEqualTo(2);
        assertThat(rankList.get(1).getConcertName()).isEqualTo("콘서트 A");
        assertThat(rankList.get(1).getViewCount()).isEqualTo(1);
    }


}