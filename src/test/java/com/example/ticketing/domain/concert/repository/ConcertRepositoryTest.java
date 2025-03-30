package com.example.ticketing.domain.concert.repository;

import com.example.ticketing.domain.concert.dto.response.ConcertRankResponse;
import com.example.ticketing.domain.concert.entity.Concert;
import com.example.ticketing.domain.concert.enums.ConcertType;
import com.example.ticketing.domain.concert.service.ConcertService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.View;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class ConcertRepositoryTest {
    @Autowired
    ConcertRepository concertRepository;
    @Autowired
    private View view;

    @Autowired
    ConcertService concertService;

//    @Test
//    void saveTestData(){
//        List<Concert> concerts = new ArrayList<>();
//
//        ConcertType[] concertTypes = ConcertType.values();
//
//        Random random = new Random(System.currentTimeMillis());
//
//        for(int i=0;i<50000;i++){
//            Concert concert = new Concert(i+1L,
//                    concertTypes[random.nextInt(concertTypes.length)],
//                    "user-" + UUID.randomUUID().toString().substring(0,8));
//            ReflectionTestUtils.setField(concert, "isDeleted", false);
//            concerts.add(concert);
//        }
//        concertRepository.saveAll(concerts);
//    }


    @Test
    @DisplayName("concertId로 콘서트 조회 테스트")
    @Transactional
    public void findByIdTest(){
        // given

        LocalDateTime localDateTime = LocalDateTime.now();

        Concert concert = Concert.builder()
                .concertName("testConcert")
                .concertType(ConcertType.MUSICAL)
                .concertDate(localDateTime)
                .ticketingDate(localDateTime)
                .maxTicketPerUser(10)
                .isDeleted(false)
                .build();

        // when
        Concert savedConcert = concertRepository.save(concert);

        Concert findConcert = concertRepository.findById(savedConcert.getId()).get();

        // then
        assertThat(findConcert.getConcertName()).isEqualTo("testConcert");
        assertThat(findConcert.getConcertType()).isEqualTo(ConcertType.MUSICAL);
        assertThat(findConcert.getConcertDate()).isEqualTo(localDateTime);
        assertThat(findConcert.getTicketingDate()).isEqualTo(localDateTime);
        assertThat(findConcert.getMaxTicketPerUser()).isEqualTo(10);
    }

    @Test
    @DisplayName("인기 콘서트 목록 조회 테스트")
    @Transactional
    public void findPopularConcertsTest(){
        // given
        List<Concert> concerts = new ArrayList<>();

        concerts.add(getConcert("콘서트1", 1000L, false));
        concerts.add(getConcert("콘서트3", 2000L, false));
        concerts.add(getConcert("콘서트2", 3000L, false));
        concerts.add(getConcert("콘서트4", 4000L, true));

        concertRepository.saveAll(concerts);

        // when
        Page<Concert> findConcerts = concertRepository.findPopularConcerts(PageRequest.of(0,4));

        // then
        assertThat(findConcerts.getContent()).hasSize(3);
        assertThat(findConcerts.getContent().get(0).getConcertName()).isEqualTo("콘서트2");
        assertThat(findConcerts.getContent().get(1).getConcertName()).isEqualTo("콘서트3");
        assertThat(findConcerts.getContent().get(2).getConcertName()).isEqualTo("콘서트1");

    }

    @Test
    @Transactional
    @DisplayName("콘서트 리스트 검색")
    void findConcertByKeywordTest(){
        // given
        List<Concert> concerts = new ArrayList<>();

        concerts.add(Concert.builder().concertName("콘서트1").build());
        concerts.add(Concert.builder().concertName("콘서트3").build());
        concerts.add(Concert.builder().concertName("콘서트2").build());
        concerts.add(Concert.builder().concertName("콘서트4").build());

        concertRepository.saveAll(concerts);

        // when
        Page<Concert> findConcerts = concertRepository.findConcertsByKeyword(PageRequest.of(0,4), null, null);

        // then
        assertThat(findConcerts.getContent()).hasSize(4);
        assertThat(findConcerts.getContent().get(0).getConcertName()).isEqualTo("콘서트1");
        assertThat(findConcerts.getContent().get(1).getConcertName()).isEqualTo("콘서트3");
        assertThat(findConcerts.getContent().get(2).getConcertName()).isEqualTo("콘서트2");
        assertThat(findConcerts.getContent().get(3).getConcertName()).isEqualTo("콘서트4");
    }

    @Test
    @Transactional
    @DisplayName("콘서트 리스트 concertName 으로 검색")
    void findConcertByKeywordWithConcertNameTest(){
        // given
        List<Concert> concerts = new ArrayList<>();

        concerts.add(Concert.builder().concertName("tgg 콘서트1").build());
        concerts.add(Concert.builder().concertName("tgg 콘서트3").build());
        concerts.add(Concert.builder().concertName("콘서트2").build());
        concerts.add(Concert.builder().concertName("콘서트4").build());

        concertRepository.saveAll(concerts);

        // when
        Page<Concert> findConcerts = concertRepository.findConcertsByKeyword(PageRequest.of(0,4), "tgg", null);

        // then
        assertThat(findConcerts.getContent()).hasSize(2);
        assertThat(findConcerts.getContent().get(0).getConcertName()).isEqualTo("tgg 콘서트1");
        assertThat(findConcerts.getContent().get(1).getConcertName()).isEqualTo("tgg 콘서트3");
    }

    @Test
    @Transactional
    @DisplayName("콘서트 리스트 concertType 으로 검색")
    void findConcertByKeywordWithConcertTypeTest(){
        // given
        List<Concert> concerts = new ArrayList<>();

        concerts.add(Concert.builder().concertType(ConcertType.MUSICAL).concertName("tgg1").build());
        concerts.add(Concert.builder().concertType(ConcertType.ETC).concertName("tgg2").build());
        concerts.add(Concert.builder().concertType(ConcertType.PLAY).concertName("tgg3").build());
        concerts.add(Concert.builder().concertType(ConcertType.MUSICAL).concertName("tgg4").build());

        concertRepository.saveAll(concerts);

        // when
        Page<Concert> findConcerts = concertRepository.findConcertsByKeyword(PageRequest.of(0,4), null, "MUSICAL");

        // then
        assertThat(findConcerts.getContent()).hasSize(2);
        assertThat(findConcerts.getContent().get(0).getConcertName()).isEqualTo("tgg1");
        assertThat(findConcerts.getContent().get(1).getConcertName()).isEqualTo("tgg4");
    }

    @Test
    @Transactional
    @DisplayName("콘서트 리스트 concertName, concertType 으로 검색")
    void findConcertByKeywordWithConcertNameAndConcertTypeTest(){
        // given
        List<Concert> concerts = new ArrayList<>();

        concerts.add(Concert.builder().concertType(ConcertType.MUSICAL).concertName("tgg1").build());
        concerts.add(Concert.builder().concertType(ConcertType.ETC).concertName("tgg2").build());
        concerts.add(Concert.builder().concertType(ConcertType.PLAY).concertName("tgg3").build());
        concerts.add(Concert.builder().concertType(ConcertType.PLAY).concertName("tgg4").build());

        concertRepository.saveAll(concerts);

        // when
        Page<Concert> findConcerts = concertRepository.findConcertsByKeyword(PageRequest.of(0,4), "tgg", "MUSICAL");

        // then
        assertThat(findConcerts.getContent()).hasSize(1);
        assertThat(findConcerts.getContent().get(0).getConcertName()).isEqualTo("tgg1");
    }

    @Test
    @Transactional
    @DisplayName("콘서트 리스트 concertDate 기준으로 오름차순 내림차순 정렬")
    void findConcertByKeywordOrderByConcertDateTest(){
        // given
        List<Concert> concerts = new ArrayList<>();

        concerts.add(Concert.builder().concertDate(LocalDateTime.parse("2022-01-21T00:00:00")).concertName("tgg1").build());
        concerts.add(Concert.builder().concertDate(LocalDateTime.parse("2022-02-21T00:00:00")).concertName("tgg2").build());
        concerts.add(Concert.builder().concertDate(LocalDateTime.parse("2022-03-21T00:00:00")).concertName("tgg3").build());
        concerts.add(Concert.builder().concertDate(LocalDateTime.parse("2022-04-21T00:00:00")).concertName("tgg4").build());

        concertRepository.saveAll(concerts);

        // when
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "concertDate");
        Page<Concert> findConcerts = concertRepository.findConcertsByKeyword(PageRequest.of(0,4, Sort.by(order)), null, null);

        // then
        assertThat(findConcerts.getContent()).hasSize(4);
        assertThat(findConcerts.getContent().get(0).getConcertName()).isEqualTo("tgg4");
        assertThat(findConcerts.getContent().get(1).getConcertName()).isEqualTo("tgg3");
        assertThat(findConcerts.getContent().get(2).getConcertName()).isEqualTo("tgg2");
        assertThat(findConcerts.getContent().get(3).getConcertName()).isEqualTo("tgg1");
    }

    private Concert getConcert(String concertName, Long viewCount, boolean isDeleted){
        Concert concert = Concert.builder()
                .concertName(concertName)
                .build();
        ReflectionTestUtils.setField(concert, "viewCount", viewCount);
        ReflectionTestUtils.setField(concert, "isDeleted", isDeleted);
        return concert;
    }
}