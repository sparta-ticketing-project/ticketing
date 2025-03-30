package com.example.ticketing.domain.seatcount.service;

import com.example.ticketing.domain.concert.entity.Concert;
import com.example.ticketing.domain.concert.repository.ConcertRepository;
import com.example.ticketing.domain.seat.entity.SeatDetail;
import com.example.ticketing.domain.seat.repository.SeatDetailRepository;
import com.example.ticketing.domain.seatcount.repository.SeatCountRedisRepository;
import com.example.ticketing.global.util.RedisKeyGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatCountSyncService {

    private final SeatCountRedisRepository redisRepository;
    private final SeatDetailRepository seatDetailRepository;
    private final ConcertRepository concertRepository;

    @Transactional
    public void syncSeatCount() {
        log.info("[seatCountSyncService] Redis -> DB 동기화 시작");

        syncSeatDetailSeatCount();
        syncConcertSeatCount();

        log.info("[seatCountSyncService] Redis -> DB 동기화 완료");
    }

    private void syncSeatDetailSeatCount() {
        Set<String> keys = redisRepository.findSeatDetailSeatCountKeys();
        List<Long> ids = extractIdsFromKeys(keys, RedisKeyGenerator.SEAT_DETAIL_COUNT_PREFIX);

        List<SeatDetail> seatDetails = seatDetailRepository.findAllById(ids);

        for (SeatDetail seatDetail : seatDetails) {
            try {
                updateSeatDetail(seatDetail);
            } catch (Exception e) {
                log.error(
                        "[seatCountSyncService] [SeatDetail] 좌석 수 동기화 실패 - seatDetailId={}, error={}",
                        seatDetail.getId(), e.getMessage(), e
                );
            }
        }
    }

    private void updateSeatDetail(SeatDetail seatDetail) {
        Long value = redisRepository.getSeatDetailSeatCount(seatDetail.getId());
        int amount = value.intValue();
        if (amount != 0) {
            seatDetail.increaseAvailableSeatCount(amount);
            redisRepository.resetSeatDetailSeatCount(seatDetail.getId());
        }
    }

    private void syncConcertSeatCount() {
        Set<String> keys = redisRepository.findConcertSeatCountKeys();
        List<Long> ids = extractIdsFromKeys(keys, RedisKeyGenerator.CONCERT_COUNT_PREFIX);

        List<Concert> concerts = concertRepository.findAllById(ids);

        for (Concert concert : concerts) {
            try {
                updateConcert(concert);
            } catch (Exception e) {
                log.error(
                        "[seatCountSyncService] [Concert] 좌석 수 동기화 실패 - concertId={}, error={}",
                        concert.getId(), e.getMessage(), e
                );
            }
        }

    }

    private void updateConcert(Concert concert) {
        Long value = redisRepository.getConcertSeatCount(concert.getId());
        int amount = value.intValue();
        if (amount != 0) {
            concert.increaseAvailableSeatCount(amount);
            redisRepository.resetConcertSeatCount(concert.getId());
        }
    }

    private List<Long> extractIdsFromKeys(Set<String> keys, String prefix) {
        return keys.stream()
                .map(key -> RedisKeyGenerator.extractIdFromKey(key, prefix))
                .toList();
    }
}
