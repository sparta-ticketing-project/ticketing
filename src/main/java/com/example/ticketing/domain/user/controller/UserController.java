package com.example.ticketing.domain.user.controller;

import com.example.ticketing.domain.user.dto.request.UserUpdateRequest;
import com.example.ticketing.domain.user.dto.response.UserResponse;
import com.example.ticketing.domain.user.dto.response.UserUpdateResponse;
import com.example.ticketing.domain.user.service.UserService;
import com.example.ticketing.global.auth.Auth;
import com.example.ticketing.global.dto.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/{id}")
public class UserController {

    private final UserService userService;

    // 유저 단건 조회
    @GetMapping
    public ResponseEntity<UserResponse> getUser(
            @Auth AuthUser authUser,
            @PathVariable Long id) {
        UserResponse response = userService.getUser(id, authUser);
        return ResponseEntity.ok(response);
    }

    // 유저 수정
    @PutMapping
    public ResponseEntity<UserUpdateResponse> updateUser(
            @Auth AuthUser authUser,
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        UserUpdateResponse response = userService.updateUser(id, request, authUser);
        return ResponseEntity.ok(response);
    }
}
