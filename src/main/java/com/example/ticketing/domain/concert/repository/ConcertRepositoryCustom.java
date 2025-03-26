package com.example.ticketing.domain.concert.repository;

import com.example.ticketing.domain.concert.entity.Concert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ConcertRepositoryCustom {
    Page<Concert> findConcertsByKeyword(Pageable pageable, String concertName, String concertType);
}
