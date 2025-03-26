package com.example.ticketing.domain.auth.controller;

import com.example.ticketing.domain.auth.dto.request.LoginRequest;
import com.example.ticketing.domain.auth.dto.request.SignUpRequest;
import com.example.ticketing.domain.auth.dto.request.UpdatePasswordRequest;
import com.example.ticketing.domain.auth.dto.request.WithDrawRequest;
import com.example.ticketing.domain.auth.dto.response.AccessTokenResponse;
import com.example.ticketing.domain.auth.dto.response.SignUpResponse;
import com.example.ticketing.domain.auth.dto.response.TokenResponse;
import com.example.ticketing.domain.auth.service.AuthService;
import com.example.ticketing.global.auth.Auth;
import com.example.ticketing.global.dto.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest dto) {
        SignUpResponse saveUser = authService.save(dto);
        return ResponseEntity.ok(saveUser);
    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(@Valid @RequestBody LoginRequest dto) {
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(7);
        TokenResponse response = authService.login(dto, expiryDate);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, response.getRefreshToken().toString())
                .body(AccessTokenResponse.toDto(response));
    }

    @PatchMapping("/password")
    public ResponseEntity<String> updatePassword(
            @Auth AuthUser authUser,
            @Valid @RequestBody UpdatePasswordRequest dto
            ) {
        authService.updatePassword(authUser, dto);
        return ResponseEntity.ok("비밀번호 변경에 성공했습니다.");
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(
            @Auth AuthUser authUser,
            @Valid @RequestBody WithDrawRequest dto
            ) {
        ResponseCookie response = authService.withdraw(authUser, dto);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, response.toString())
                .body("회원탈퇴에 성공했습니다.");
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refresh(
            @CookieValue(value = "refreshToken", required = false) String refreshToken
    ) {
        AccessTokenResponse response = authService.refresh(refreshToken);
        return ResponseEntity.ok(response);
    }
}
