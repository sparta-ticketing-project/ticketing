package com.example.ticketing.domain.concert.controller;

import com.example.ticketing.domain.concert.dto.response.*;
import com.example.ticketing.domain.concert.service.ConcertService;
import com.example.ticketing.global.auth.Auth;
import com.example.ticketing.global.dto.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class ConcertController {
    private final ConcertService concertService;

    @GetMapping("/v1/concerts/popular")
    public ResponseEntity<Page<ConcertRankResponse>> findPopularConcertsV1(@RequestParam(defaultValue = "10") Integer limit){
        return ResponseEntity.ok(concertService.findPopularConcertsV1(limit));
    }

    @GetMapping("/v2/concerts/popular")
    public ResponseEntity<List<ConcertRedisRankResponse>> findPopularConcertsV2(@RequestParam(defaultValue = "10") Integer limit){
        return ResponseEntity.ok(concertService.findPopularConcertsV2(limit));
    }

    @GetMapping("/v1/concerts/{concertId}")
    public ResponseEntity<ConcertSingleResponse> findSingleConcert(@Auth AuthUser authUser, @PathVariable Long concertId){
        return ResponseEntity.ok(concertService.findSingleConcert(authUser.getUserId(), concertId));
    }

    @GetMapping("/v1/concerts")
    public ResponseEntity<Page<ConcertSearchResponse>> findConcertsByKeyword(@PageableDefault(size=10,page=0)Pageable pageable,
                                                                             @RequestParam(required = false) String concertName,
                                                                             @RequestParam(required = false) String concertType){
        return ResponseEntity.ok(concertService.findConcertsByKeyword(pageable, concertName, concertType));
    }
}
