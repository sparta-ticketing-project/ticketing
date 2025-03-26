package com.example.ticketing.domain.auth.controller;

import com.example.ticketing.domain.auth.dto.request.LoginRequest;
import com.example.ticketing.domain.auth.dto.request.SignUpRequest;
import com.example.ticketing.domain.auth.dto.response.AccessTokenResponse;
import com.example.ticketing.domain.auth.dto.response.SignUpResponse;
import com.example.ticketing.domain.auth.dto.response.TokenResponse;
import com.example.ticketing.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
