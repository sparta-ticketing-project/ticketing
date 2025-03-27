package com.example.ticketing.domain.seat.service;

import com.example.ticketing.domain.concert.entity.Concert;
import com.example.ticketing.domain.seat.dto.response.*;
import com.example.ticketing.domain.seat.entity.Seat;
import com.example.ticketing.domain.seat.entity.SeatDetail;
import com.example.ticketing.domain.concert.repository.ConcertRepository;
import com.example.ticketing.domain.seat.repository.SeatDetailRepository;
import com.example.ticketing.domain.seat.repository.SeatRepository;
import com.example.ticketing.global.exception.CustomException;
import com.example.ticketing.global.exception.ExceptionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final SeatDetailRepository seatDetailRepository;

    @Transactional(readOnly = true)
    public SeatResponse getSeats(Long concertId, int page, int pageSize) {
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new CustomException(ExceptionType.CONCERT_NOT_FOUND));

        if (concert.isDeleted()) {
            throw new CustomException(ExceptionType.CONCERT_NOT_FOUND);
        }

        Page<Seat> seatPage = seatRepository.findByConcertId(concertId, PageRequest.of(page - 1, pageSize));
        List<SeatDetail> seatDetails = seatDetailRepository.findByConcertId(concertId);

        List<SeatDetailResponse> seatDetailResponses = seatDetails.stream()
                .map(sd -> new SeatDetailResponse(sd.getId(), sd.getSeatType().name(), sd.getPrice()))
                .collect(Collectors.toList());

        List<SeatItemResponse> seatItems = seatPage.getContent().stream()
                .map(seat -> new SeatItemResponse(seat.getId(), seat.getSeatNumber(),
                        seat.getSeatDetail() != null ? seat.getSeatDetail().getId() : null))
                .collect(Collectors.toList());

        return new SeatResponse(
                new ConcertResponse(concert.getId(), concert.getConcertName(), concert.getConcertDate(), concert.getTicketingDate(), concert.getMaxTicketPerUser()),
                seatDetailResponses,
                new SeatPageResponse(page, pageSize, seatPage.getTotalElements(), seatPage.getTotalPages(), seatItems)
        );
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

        return new SeatItemDetailResponse(
                seat.getId(),
                seat.getConcert().getId(),
                seatDetail.getId(),
                seatDetail.getSeatType(),
                seatDetail.getPrice(),
                seat.isAvailable(),
                seat.getSeatNumber()
        );
    }
}