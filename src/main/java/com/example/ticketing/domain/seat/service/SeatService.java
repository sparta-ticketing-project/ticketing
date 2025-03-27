package com.example.ticketing.domain.seat.service;

import com.example.ticketing.domain.concert.entity.Concert;
import com.example.ticketing.domain.concert.repository.ConcertRepository;
import com.example.ticketing.domain.seat.entity.SeatDetail;
import com.example.ticketing.domain.seat.repository.SeatDetailRepository;
import com.example.ticketing.domain.seat.repository.SeatRepository;
import com.example.ticketing.global.exception.CustomException;
import com.example.ticketing.global.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatDetailRepository seatDetailRepository;
    private final SeatRepository seatRepository;
    private final ConcertRepository concertRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateAvailableSeatCount(int changedSeatCount, Long seatDetailId, Long concertId, String operation) {

        SeatDetail seatDetail = seatDetailRepository.findById(seatDetailId).orElseThrow(
                () -> new CustomException(ExceptionType.SEAT_DETAIL_NOT_FOUND)
        );
        int seatDetailAvailableSeatCount = seatDetail.getAvailableSeatCount();

        Concert concert = concertRepository.findById(concertId).orElseThrow(
                () -> new CustomException(ExceptionType.CONCERT_NOT_FOUND)
        );
        int totalAvailableSeatCount = concert.getAvailableSeatCount();

        switch (operation) {
            case "+":
                seatDetailAvailableSeatCount += changedSeatCount;
                totalAvailableSeatCount += changedSeatCount;
                seatDetail.setAvailableSeatCount(seatDetailAvailableSeatCount);
                concert.setAvailableSeatCount(totalAvailableSeatCount);
                break;

            case "-":
                seatDetailAvailableSeatCount -= changedSeatCount;
                totalAvailableSeatCount -= changedSeatCount;
                seatDetail.setAvailableSeatCount(seatDetailAvailableSeatCount);
                concert.setAvailableSeatCount(totalAvailableSeatCount);
                break;

            default:
                throw new IllegalArgumentException("Invalid operation: " + operation);
        }
    }
}
