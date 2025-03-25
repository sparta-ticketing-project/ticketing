package com.example.ticketing.domain.auth.controller;

import com.example.ticketing.domain.auth.dto.request.SignUpRequest;
import com.example.ticketing.domain.auth.dto.response.SignUpResponse;
import com.example.ticketing.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
