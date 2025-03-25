package com.example.ticketing.domain.concert.service;

import com.example.ticketing.domain.concert.dto.response.ConcertRankResponse;
import com.example.ticketing.domain.concert.dto.response.ConcertSeatDetailResponse;
import com.example.ticketing.domain.concert.dto.response.ConcertSingleResponse;
import com.example.ticketing.domain.concert.entity.Concert;
import com.example.ticketing.domain.concert.repository.ConcertRepository;
import com.example.ticketing.domain.seat.entity.SeatDetail;
import com.example.ticketing.domain.seat.repository.SeatDetailRepository;
import com.example.ticketing.global.exception.CustomException;
import com.example.ticketing.global.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConcertService {
    private final ConcertRepository concertRepository;
    private final SeatDetailRepository seatDetailRepository;

    @Transactional(readOnly = true)
    public Page<ConcertRankResponse> findPopularConcertsV1(Integer limit){
        Pageable pageable = PageRequest.of(0, limit);

        List<ConcertRankResponse> concertRankResponses = new ArrayList<>();

        Page<Concert> concerts = concertRepository.findPopularConcerts(pageable);

        Integer rank = 1;
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

//    @Cacheable
//    @Transactional(readOnly = true)
//    public Page<ConcertRankResponse> findPopularConcertsV2(Integer limit){
//        Pageable pageable = PageRequest.of(0, limit);
//
//        List<ConcertRankResponse> concertRankResponses = new ArrayList<>();
//
//        Page<Concert> concerts = concertRepository.findPopularConcerts(pageable);
//
//        Integer rank = 1;
//        for(Concert concert : concerts){
//            concertRankResponses.add(ConcertRankResponse.builder()
//                    .rank(rank++)
//                    .viewCount(concert.getViewCount())
//                    .concertId(concert.getId())
//                    .concertName(concert.getConcertName())
//                    .build());
//        }
//
//        return new PageImpl<>(concertRankResponses, concerts.getPageable(), concerts.getTotalElements());
//    }

    @Transactional(readOnly = true)
    public ConcertSingleResponse findSingleConcert(Long concertId){
        Concert concert = concertRepository.findById(concertId).orElseThrow(() -> new CustomException(ExceptionType.CONCERT_NOT_FOUND));

        List<SeatDetail> seatDetails = seatDetailRepository.findSeatDetailsByConcertId(concertId);

        List<ConcertSeatDetailResponse> concertSeatDetailResponses = seatDetails.stream().map(seatDetail -> ConcertSeatDetailResponse.builder()
                .seatType(seatDetail.getSeatType().name())
                .price(seatDetail.getPrice())
                .totalSeatCount(seatDetail.getTotalSeatCount())
                .availableSeatCount(seatDetail.getAvailableSeatCount())
                .build()).collect(Collectors.toList());

        return ConcertSingleResponse.builder()
                .id(concert.getId())
                .concertName(concert.getConcertName())
                .concertType(concert.getConcertType().getDescription())
                .concertDate(concert.getConcertDate())
                .ticketingDate(concert.getTicketingDate())
                .totalSeatCount(concert.getTotalSeatCount())
                .availableSeatCount(concert.getAvailableSeatCount())
                .concertSeatDetailResponses(concertSeatDetailResponses)
                .build();
    }
}
