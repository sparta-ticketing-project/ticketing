package com.example.ticketing.domain.user.controller;

import com.example.ticketing.domain.user.dto.request.CreateConcertRequest;
import com.example.ticketing.domain.user.dto.request.UpdateConcertRequest;
import com.example.ticketing.domain.user.dto.request.UpdateSeatRequest;
import com.example.ticketing.domain.user.dto.response.ConcertResponse;
import com.example.ticketing.domain.user.dto.response.GenderBookingRateResponse;
import com.example.ticketing.domain.user.dto.response.UpdatedSeatResponse;
import com.example.ticketing.domain.user.entity.User;
import com.example.ticketing.domain.user.service.UserAdminService;
import com.example.ticketing.global.auth.Auth;
import com.example.ticketing.global.dto.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
// + ADMIN 권한 검증 추가 해야 함
public class UserAdminController {

    private final UserAdminService userAdminService;

    // 콘서트 생성
    @PostMapping("/api/v1/admin/concerts")
    public ResponseEntity<ConcertResponse> createConcert(
            @Auth AuthUser authUser,
            @Valid @RequestBody CreateConcertRequest dto ) {
        ConcertResponse concert = userAdminService.createConcert(
                authUser,
                dto.getConcertName(),
                dto.getConcertDate(),
                dto.getTicketingDate(),
                dto.getConcertType(),
                dto.getMaxTicketPerUser(),
                dto.getSeatDetail()
        );
        return ResponseEntity.ok(concert);
    }

    // 콘서트 다건 조회
    @GetMapping("/api/v1/admin/concerts")
    public ResponseEntity<Page<ConcertResponse>> getConcerts(
            @Auth AuthUser authUser,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ConcertResponse> concerts = userAdminService.getConcerts(authUser, page, size);

        return ResponseEntity.ok(concerts);

    }

    // 콘서트 상세 조회
    @GetMapping("/api/v1/admin/concerts/{concertId}")
    public ResponseEntity<ConcertResponse> getConcert(
            @Auth AuthUser authUser,
            @PathVariable Long concertId
    ) {
        ConcertResponse concert = userAdminService.getConcert(authUser, concertId);
        return ResponseEntity.ok(concert);
    }

    // 콘서트 정보 수정 (자신이 생성한 콘서트만 수정)
    @PatchMapping("/api/v1/admin/concerts/{concertId}")
    public ResponseEntity<ConcertResponse> updateConcert(
            @Auth AuthUser authUser,
            @Valid @RequestBody UpdateConcertRequest dto,
            @PathVariable Long concertId) {
        ConcertResponse concert = userAdminService.updateConcert(
                authUser,
                concertId,
                dto.getConcertName(),
                dto.getConcertDate(),
                dto.getTicketingDate(),
                dto.getConcertType(),
                dto.getMaxTicketPerUser(),
                dto.getSeatDetail()
        );

        return ResponseEntity.ok(concert);
    }

    // 콘서트 삭제 (자신이 생성한 콘서트만 삭제)
    @PostMapping("/api/v1/admin/concerts/{concertId}")
    public ResponseEntity<Map<String, Object>> deleteConcert(
            @Auth AuthUser authUser,
            @PathVariable Long concertId
    ) {
        userAdminService.deleteConcert(authUser, concertId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "콘서트가 삭제되었습니다.");
        response.put("deletedConcertId", concertId);

        return ResponseEntity.ok(response);
    }

    // 좌석 수정
    @PatchMapping("/api/v1/admin/concerts/{concertId}/seats/{seatDetailId}/seat-update")
    public ResponseEntity<UpdatedSeatResponse> updateSeats(
            @Auth AuthUser authUser,
            @RequestBody UpdateSeatRequest dto,
            @PathVariable Long concertId,
            @PathVariable Long seatDetailId) {
        UpdatedSeatResponse result = userAdminService.updateSeats(authUser, concertId, seatDetailId, dto);
        return ResponseEntity.ok(result);
    }

    // 티켓 예매율 By Gender
    @GetMapping("/api/v1/admin/concerts/{concertId}/booking-rate-by-gender")
    public ResponseEntity<GenderBookingRateResponse> bookingRateByGender(
            @Auth AuthUser authUser,
            @PathVariable Long concertId
    ) {
        GenderBookingRateResponse result = userAdminService.bookingRateByGender(authUser, concertId);
        return ResponseEntity.ok(result);
    }
}
