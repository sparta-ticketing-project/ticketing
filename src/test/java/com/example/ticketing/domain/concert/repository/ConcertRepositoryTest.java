package com.example.ticketing.domain.concert.repository;

import com.example.ticketing.domain.concert.entity.Concert;
import com.example.ticketing.domain.concert.service.ConcertService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ConcertRepositoryTest {
    @Autowired
    ConcertRepository concertRepository;

    @Autowired
    ConcertService concertService;

    @Test
    void saveTestData(){
        for(int i=0;i<100;i++){
            Concert concert = new Concert(i+1L);
            concertRepository.save(concert);
        }
    }

    @Test
    void getPopularConcerts(){
        Pageable pageable = PageRequest.of(0,20);
        Page<Concert> concerts = concertService.findPopularConcerts(pageable);
        for(Concert concert : concerts){
            System.out.println(concert.getId() + "번 콘서트, 조회수 = " + concert.getViewCount());
        }
    }
}