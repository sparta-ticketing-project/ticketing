package com.example.ticketing.domain.seat.controller;

import com.example.ticketing.domain.seat.dto.response.SeatResponse;
import com.example.ticketing.domain.seat.service.SeatService;
import com.example.ticketing.global.exception.CustomException;
import com.example.ticketing.global.exception.ExceptionType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/concerts/{concertId}/seats")
@RequiredArgsConstructor
public class SeatController {
    private final SeatService seatService;

    @GetMapping
    public ResponseEntity<SeatResponse> getSeats(@PathVariable Long concertId,
                                                 @RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "20") int pageSize) {

        if (page < 1) {
            throw new CustomException(ExceptionType.INVALID_PAGE_NUMBER);
        }
        if (pageSize < 1 || pageSize > 100) {
            throw new CustomException(ExceptionType.INVALID_PAGE_SIZE);
        }

        SeatResponse response = seatService.getSeats(concertId, page, pageSize);
        return ResponseEntity.ok(response);
    }
}