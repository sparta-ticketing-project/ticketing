package com.example.ticketing.domain.concert.repository;

import com.example.ticketing.domain.concert.dto.response.ConcertRankResponse;
import com.example.ticketing.domain.concert.entity.Concert;
import com.example.ticketing.domain.concert.enums.ConcertType;
import com.example.ticketing.domain.concert.service.ConcertService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ConcertRepositoryTest {
    @Autowired
    ConcertRepository concertRepository;

    @Autowired
    ConcertService concertService;

    @Test
    void saveTestData(){
        List<Concert> concerts = new ArrayList<>();
        for(int i=0;i<50000;i++){
            Concert concert = new Concert(i+1L, ConcertType.MUSICAL);
            concerts.add(concert);
        }
        concertRepository.saveAll(concerts);
    }

}