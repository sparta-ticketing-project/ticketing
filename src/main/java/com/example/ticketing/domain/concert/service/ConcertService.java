package com.example.ticketing.domain.concert.service;

import com.example.ticketing.domain.concert.dto.response.ConcertRankResponse;
import com.example.ticketing.domain.concert.entity.Concert;
import com.example.ticketing.domain.concert.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertService {
    private final ConcertRepository concertRepository;

    public Page<ConcertRankResponse> findPopularConcerts(Integer limit){
        Pageable pageable = PageRequest.of(0, limit);

        List<ConcertRankResponse> concertRankResponses = new ArrayList<>();

        Page<Concert> concerts = concertRepository.findPopularConcerts(pageable);

        Long rank = 1L;
        for(Concert concert : concerts){
            concertRankResponses.add(ConcertRankResponse.builder()
                    .rank(rank++)
                    .viewCount(concert.getViewCount())
                    .concertId(concert.getId())
                    .concertName(concert.getConcertName())
                    .build());
        }

        return new PageImpl<>(concertRankResponses, concerts.getPageable(), concerts.getTotalElements());
    }
}
