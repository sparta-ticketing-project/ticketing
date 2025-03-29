package com.example.ticketing.domain.concert.service;

import com.example.ticketing.domain.concert.dto.response.ConcertRankResponse;
import com.example.ticketing.domain.concert.dto.response.ConcertRedisRankResponse;
import com.example.ticketing.domain.concert.dto.response.ConcertSearchResponse;
import com.example.ticketing.domain.concert.dto.response.ConcertSingleResponse;
import com.example.ticketing.domain.concert.entity.Concert;
import com.example.ticketing.domain.concert.enums.ConcertType;
import com.example.ticketing.domain.concert.repository.ConcertRedisRepository;
import com.example.ticketing.domain.concert.repository.ConcertRepository;
import com.example.ticketing.domain.seat.entity.SeatDetail;
import com.example.ticketing.domain.seat.enums.SeatType;
import com.example.ticketing.domain.seat.repository.SeatDetailRepository;
import com.example.ticketing.global.exception.CustomException;
import com.example.ticketing.global.exception.ExceptionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class ConcertServiceTest {
    @Mock
    ConcertRepository concertRepository;

    @Mock
    ConcertRedisRepository concertRedisRepository;

    @Mock
    SeatDetailRepository seatDetailRepository;

    @InjectMocks
    ConcertService concertService;

    @Test
    @DisplayName("findPopularConcertV1() 테스트")
    void findPopularConcertsV1Test(){
        // given
        Pageable pageable = PageRequest.of(0, 10);

        List<Concert> concerts = new ArrayList<>();
        long viewCount = 5;
        for(int i=1;i<=5;i++){
            Concert concert = Concert.builder()
                    .concertName("concert" + (i))
                    .build();

            ReflectionTestUtils.setField(concert, "viewCount", viewCount - i);
            ReflectionTestUtils.setField(concert, "id", (long)i);
            concerts.add(concert);
        }

        given(concertRepository.findPopularConcerts(pageable)).willReturn(new PageImpl<>(concerts, pageable, concerts.size()));

        // when
        Page<ConcertRankResponse> concertRankResponses = concertService.findPopularConcertsV1(10);

        // then
        assertThat(concertRankResponses.getContent()).hasSize(5);
        assertThat(concertRankResponses.getTotalElements()).isEqualTo(5);
        for(int i=1;i<=5;i++){
            ConcertRankResponse concertRankResponse = concertRankResponses.getContent().get(i-1);
            assertThat(concertRankResponse.getConcertId()).isEqualTo(i);
            assertThat(concertRankResponse.getConcertName()).isEqualTo("concert" + i);
            assertThat(concertRankResponse.getRank()).isEqualTo(i);
            assertThat(concertRankResponse.getViewCount()).isEqualTo(viewCount - i);
        }
    }

    @Test
    @DisplayName("findPopularConcertsV2() 테스트")
    void findConcertsByKeywordTest(){
        // given
        List<ConcertRedisRankResponse> concertRedisRankResponses = new ArrayList<>();
        long viewCount = 5;
        for(int i=1;i<=5;i++){
            ConcertRedisRankResponse concertRedisRankResponse = ConcertRedisRankResponse.builder()
                    .rank(i)
                    .concertName("concert"+i)
                    .concertId((long)i)
                    .viewCount(viewCount - i)
                    .build();
            concertRedisRankResponses.add(concertRedisRankResponse);
        }

        given(concertRedisRepository.getRankList(10)).willReturn(concertRedisRankResponses);

        // when
        List<ConcertRedisRankResponse> results = concertService.findPopularConcertsV2(10);

        // then
        for(int i=1;i<=5;i++){
            ConcertRedisRankResponse concertRedisRankResponse = results.get(i-1);
            assertThat(concertRedisRankResponse.getRank()).isEqualTo(i);
            assertThat(concertRedisRankResponse.getConcertName()).isEqualTo("concert"+i);
            assertThat(concertRedisRankResponse.getConcertId()).isEqualTo(i);
            assertThat(concertRedisRankResponse.getViewCount()).isEqualTo(viewCount-i);
        }
    }

    @Test
    @DisplayName("findSingleConcert() 콘서트가 존재하지 않을 때")
    void findSingleConcertFailTest(){
        // given
        Long concertId = 1L;
        Long userId = 1L;

        given(concertRepository.findById(concertId)).willReturn(Optional.empty());

        // when
        CustomException customException = assertThrows(CustomException.class, () ->  concertService.findSingleConcert(userId, concertId));

        // then
        assertThat(customException).hasMessage("해당 콘서트를 찾을 수 없습니다.");
        assertThat(customException.getExceptionType()).isEqualTo(ExceptionType.CONCERT_NOT_FOUND);
        assertThat(customException.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("findSingleConcert() 콘서트가 존재할 때")
    void findSingleConcertTest(){
        // given
        Long concertId = 1L;
        Long userId = 1L;
        Long viewCount = 100L;
        LocalDateTime localDateTime = LocalDateTime.now();

        Concert concert = Concert.builder()
                .concertName("concert")
                .concertType(ConcertType.of("musical"))
                .concertDate(localDateTime)
                .ticketingDate(localDateTime)
                .build();

        ReflectionTestUtils.setField(concert, "id", userId);
        ReflectionTestUtils.setField(concert, "totalSeatCount", 1000);
        ReflectionTestUtils.setField(concert, "availableSeatCount", 1000);
        ReflectionTestUtils.setField(concert, "viewCount", viewCount);

        given(concertRepository.findById(concertId)).willReturn(Optional.of(concert));

        given(concertRedisRepository.isFirstView(userId, concertId)).willReturn(true);
        doNothing().when(concertRedisRepository).incrementViewCount(concertId);
        doNothing().when(concertRedisRepository).cacheConcertName(concertId, concert.getConcertName());

        List<SeatDetail> seatDetails = new ArrayList<>();
        seatDetails.add(SeatDetail.builder().seatType(SeatType.A_GRADE).build());
        given(seatDetailRepository.findSeatDetailsByConcertId(concertId)).willReturn(seatDetails);

        // when
        ConcertSingleResponse concertSingleResponse = concertService.findSingleConcert(userId, concertId);

        // then
        assertThat(concertSingleResponse.getId()).isEqualTo(userId);
        assertThat(concertSingleResponse.getConcertName()).isEqualTo("concert");
        assertThat(concertSingleResponse.getConcertType()).isEqualTo(ConcertType.MUSICAL.getDescription());
        assertThat(concertSingleResponse.getConcertDate()).isEqualTo(localDateTime);
        assertThat(concertSingleResponse.getTicketingDate()).isEqualTo(localDateTime);
        assertThat(concertSingleResponse.getTotalSeatCount()).isEqualTo(1000);
        assertThat(concertSingleResponse.getAvailableSeatCount()).isEqualTo(1000);
        assertThat(concertSingleResponse.getViewCount()).isEqualTo(viewCount+1);
        assertThat(concertSingleResponse.getConcertSeatDetailResponses()).hasSize(1);
    }

    @Test
    @DisplayName("findConcertsByKeyword 테스트")
    void findConcertByKeywordTest(){
        // given
        Pageable pageable = PageRequest.of(0, 10);
        String concertName = "concert";
        String concertType = "musical";
        LocalDateTime localDateTime = LocalDateTime.now();

        List<Concert> concerts = new ArrayList<>();
        for(int i=1;i<=10;i++){
            Concert concert = Concert.builder()
                    .concertName("concert"+i)
                    .concertType(ConcertType.of(concertType))
                    .concertDate(localDateTime)
                    .ticketingDate(localDateTime)
                    .build();

            ReflectionTestUtils.setField(concert, "id", (long)i);
            ReflectionTestUtils.setField(concert, "totalSeatCount", 1000);
            ReflectionTestUtils.setField(concert, "availableSeatCount", 1000);
            ReflectionTestUtils.setField(concert, "viewCount", (long)i);

            concerts.add(concert);
        }

        Page<Concert> concertPage = new PageImpl<>(concerts, pageable, concerts.size());

        given(concertRepository.findConcertsByKeyword(pageable, concertName, concertType)).willReturn(concertPage);

        // when
        Page<ConcertSearchResponse> concertSearchResponses = concertService.findConcertsByKeyword(pageable, concertName, concertType);

        // then
        assertThat(concertSearchResponses.getContent()).hasSize(10);
        assertThat(concertSearchResponses.getTotalElements()).isEqualTo(10);
        for(int i=1;i<=10;i++){
            ConcertSearchResponse concertSearchResponse = concertSearchResponses.getContent().get(i-1);
            assertThat(concertSearchResponse.getId()).isEqualTo(i);
            assertThat(concertSearchResponse.getConcertName()).isEqualTo("concert"+i);
            assertThat(concertSearchResponse.getConcertType()).isEqualTo(ConcertType.of(concertType).getDescription());
            assertThat(concertSearchResponse.getConcertDate()).isEqualTo(localDateTime);
            assertThat(concertSearchResponse.getTicketingDate()).isEqualTo(localDateTime);
            assertThat(concertSearchResponse.getTotalSeatCount()).isEqualTo(1000);
            assertThat(concertSearchResponse.getAvailableSeatCount()).isEqualTo(1000);
            assertThat(concertSearchResponse.getViewCount()).isEqualTo(i);
        }
    }
}