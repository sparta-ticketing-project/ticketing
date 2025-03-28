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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatService {
    private final SeatRepository seatRepository;
    private final ConcertRepository concertRepository;


    @Transactional(readOnly = true)
    public SeatPageResponse getSeats(Long concertId, int page, int pageSize, Long seatDetailId) {
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new CustomException(ExceptionType.CONCERT_NOT_FOUND));

        if (concert.isDeleted()) {
            throw new CustomException(ExceptionType.CONCERT_NOT_FOUND);
        }

        Page<Seat> seatPage;

        if (seatDetailId != null) {
            // seatDetailId가 제공된 경우, 해당 seatDetailId를 기준으로 페이징
            seatPage = seatRepository.findByConcertIdAndSeatDetailIdAndIsAvailableTrue(
                    concertId, seatDetailId, PageRequest.of(page - 1, pageSize, Sort.by("seatNumber")));
        } else {
            // seatDetailId가 제공되지 않은 경우, 기존처럼 모든 좌석을 페이징
            seatPage = seatRepository.findByConcertIdAndIsAvailableTrue(
                    concertId, PageRequest.of(page - 1, pageSize, Sort.by("seatNumber")));
        }

        List<SeatPageDataResponse> seatPageDataResponses = seatPage.getContent().stream()
                .map(seat -> SeatPageDataResponse.builder()
                        .seatId(seat.getId())
                        .isAvailable(seat.isAvailable())
                        .seatNumber(seat.getSeatNumber())
                        .seatType(seat.getSeatDetail().getSeatType())
                        .price(seat.getSeatDetail().getPrice())
                        .build())
                .collect(Collectors.toList());

        return SeatPageResponse.builder()
                .concert(ConcertResponse.builder()
                        .concertId(concert.getId())
                        .concertName(concert.getConcertName())
                        .concertDate(concert.getConcertDate())
                        .ticketingDate(concert.getTicketingDate())
                        .maxTicketPerUser(concert.getMaxTicketPerUser())
                        .build())
                .seatPageDataResponses(seatPageDataResponses)
                .build();

    }

    @Transactional(readOnly = true)
    public SeatOneResponse getSeat(Long concertId, Long seatId) {
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

        return SeatOneResponse.builder()
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

