package com.example.ticketing.domain.seatcount.service;

import com.example.ticketing.domain.seatcount.dto.request.SeatCountUpdateRequest;
import com.example.ticketing.domain.seatcount.repository.SeatCountRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeatCountCacheService {

    private final SeatCountRedisRepository redisRepository;

    public void updateSeatCount(SeatCountUpdateRequest request) {
        request.getSeatDetailCountChange().getSeatDetailIdToCount()
                .forEach((seatDetailId, count) -> {
                    redisRepository.incrementSeatDetailCount(seatDetailId, count);
                });

        redisRepository.incrementConcertCount(
                request.getConcertSeatCountChange().getConcertId(),
                request.getConcertSeatCountChange().getCount()
        );
    }
}
