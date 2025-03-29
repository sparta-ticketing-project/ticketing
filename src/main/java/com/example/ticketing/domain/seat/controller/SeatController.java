package com.example.ticketing.domain.seat.controller;

import com.example.ticketing.domain.seat.dto.response.SeatOneResponse;
import com.example.ticketing.domain.seat.dto.response.SeatPageResponse;
import com.example.ticketing.domain.seat.service.SeatService;
import com.example.ticketing.global.auth.Auth;
import com.example.ticketing.global.dto.AuthUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SeatController {
    private final SeatService seatService;

    @GetMapping("/api/v1/concerts/{concertId}/seats")
    public ResponseEntity<SeatPageResponse> getSeats(
            @Auth AuthUser authUser,
            @PathVariable Long concertId,
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam(required = false) Long seatDetailId
    ) {
        SeatPageResponse seatPageResponse = seatService.getSeats(authUser.getUserId(), concertId, page, pageSize, seatDetailId);
        return ResponseEntity.ok(seatPageResponse);
    }

    @GetMapping("/api/v1/concerts/{concertId}/seats/{seatId}")
    public ResponseEntity<SeatOneResponse> getSeat(
            @Auth AuthUser authUser,
            @PathVariable Long concertId,
            @PathVariable Long seatId
    ) {
        SeatOneResponse response = seatService.getSeat(authUser.getUserId(), concertId, seatId);
        return ResponseEntity.ok(response);
    }
}