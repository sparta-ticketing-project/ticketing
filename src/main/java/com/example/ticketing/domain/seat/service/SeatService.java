package com.example.ticketing.domain.seat.service;

import com.example.ticketing.domain.concert.entity.Concert;
import com.example.ticketing.domain.seat.dto.response.*;
import com.example.ticketing.domain.seat.entity.Seat;
import com.example.ticketing.domain.seat.entity.SeatDetail;
import com.example.ticketing.domain.concert.repository.ConcertRepository;
import com.example.ticketing.domain.seat.repository.SeatRepository;
import com.example.ticketing.global.exception.CustomException;
import com.example.ticketing.global.exception.ExceptionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatService {
    private final SeatRepository seatRepository;
    private final ConcertRepository concertRepository;


    @Transactional(readOnly = true)
    public SeatResponse getSeats(Long concertId, int page, int pageSize) {
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new CustomException(ExceptionType.CONCERT_NOT_FOUND));

        if (concert.isDeleted()) {
            throw new CustomException(ExceptionType.CONCERT_NOT_FOUND);
        }

        Page<Seat> seatPage = seatRepository.findByConcertId(concertId, PageRequest.of(page - 1, pageSize));

        List<SeatDetailResponse> seatDetailResponses = seatPage.getContent().stream()
                .map(Seat::getSeatDetail)
                .filter(Objects::nonNull)
                .distinct()
                .map(sd -> SeatDetailResponse.builder()
                            .id(sd.getId())
                            .seatType(sd.getSeatType().name())
                            .price(sd.getPrice())
                            .build())
                .collect(Collectors.toList());

        List<SeatItemResponse> seatItems = seatPage.getContent().stream()
                .map(seat -> SeatItemResponse.builder()
                            .seatId(seat.getId())
                            .seatNumber(seat.getSeatNumber())
                            .seatDetailId(seat.getSeatDetail() != null ? seat.getSeatDetail().getId() : null)
                            .build())
                .collect(Collectors.toList());

        return SeatResponse.builder()
                .concert(ConcertResponse.builder()
                        .concertId(concert.getId())
                        .concertName(concert.getConcertName())
                        .concertDate(concert.getConcertDate())
                        .ticketingDate(concert.getTicketingDate())
                        .maxTicketPerUser(concert.getMaxTicketPerUser())
                        .build())
                .seatDetails(seatDetailResponses)
                .seats(SeatPageResponse.builder()
                        .page(page)
                        .pageSize(pageSize)
                        .totalCount(seatPage.getTotalElements())
                        .totalPages(seatPage.getTotalPages())
                        .items(seatItems)
                        .build())
                .build();
    }

    @Transactional(readOnly = true)
    public SeatItemDetailResponse getSeat(Long concertId, Long seatId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new CustomException(ExceptionType.SEAT_NOT_FOUND));

        if (seat.getConcert() == null || seat.getConcert().isDeleted()) {
            throw new CustomException(ExceptionType.CONCERT_NOT_FOUND);
        }

        if (!concertId.equals(seat.getConcert().getId())) {
            throw new CustomException(ExceptionType.CONCERT_SEAT_MISMATCH);
        }

        SeatDetail seatDetail = seat.getSeatDetail();
        if (seatDetail == null) {
            throw new CustomException(ExceptionType.SEAT_DETAIL_NOT_FOUND);
        }

        return SeatItemDetailResponse.builder()
                .seatId(seat.getId())
                .concertId(seat.getConcert().getId())
                .seatDetailId(seatDetail.getId())
                .seatType(seatDetail.getSeatType())
                .price(seatDetail.getPrice())
                .isAvailable(seat.isAvailable())
                .seatNumber(seat.getSeatNumber())
                .build();
    }
}

