package com.example.ticketing.domain.seat.controller;

import com.example.ticketing.domain.seat.dto.response.SeatOneResponse;
import com.example.ticketing.domain.seat.dto.response.SeatPageResponse;
import com.example.ticketing.domain.seat.service.SeatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SeatController {
    private final SeatService seatService;

    @GetMapping("/api/v1/concerts/{concertId}/seats")
    public ResponseEntity<SeatPageResponse> getSeats(
            @PathVariable Long concertId,
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam(required = false) Long seatDetailId // seatDetailId를 선택적으로 받음
    ) {
        SeatPageResponse seatPageResponse = seatService.getSeats(concertId, page, pageSize, seatDetailId);
        return ResponseEntity.ok(seatPageResponse);
    }

    @GetMapping("/api/v1/concerts/{concertId}/seats/{seatId}")
    public ResponseEntity<SeatOneResponse> getSeat(@PathVariable Long concertId,
                                                   @PathVariable Long seatId) {
        SeatOneResponse response = seatService.getSeat(concertId, seatId);
        return ResponseEntity.ok(response);
    }
}