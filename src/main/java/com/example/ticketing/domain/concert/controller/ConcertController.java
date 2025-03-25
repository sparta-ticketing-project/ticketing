package com.example.ticketing.domain.concert.controller;

import com.example.ticketing.domain.concert.dto.response.ConcertRankResponse;
import com.example.ticketing.domain.concert.dto.response.ConcertSeatDetailResponse;
import com.example.ticketing.domain.concert.dto.response.ConcertSingleResponse;
import com.example.ticketing.domain.concert.service.ConcertService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class ConcertController {
    private final ConcertService concertService;

    @GetMapping("/v1/concerts/popular")
    public ResponseEntity<Page<ConcertRankResponse>> findPopularConcerts(@RequestParam(defaultValue = "10") Integer limit){
        return ResponseEntity.ok(concertService.findPopularConcertsV1(limit));
    }

    @GetMapping("/v1/concerts/{concertId}")
    public ResponseEntity<ConcertSingleResponse> findSingleConcert(@PathVariable Long concertId){
        return ResponseEntity.ok(concertService.findSingleConcert(concertId));
    }
}
