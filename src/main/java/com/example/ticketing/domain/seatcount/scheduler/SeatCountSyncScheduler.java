package com.example.ticketing.domain.seatcount.scheduler;

import com.example.ticketing.domain.seatcount.service.SeatCountSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeatCountSyncScheduler {

    private final SeatCountSyncService seatCountSyncService;

    @Scheduled(fixedDelay = 5000)
    public void syncSeatCountToDb() {
        seatCountSyncService.syncSeatCount();
    }
}
