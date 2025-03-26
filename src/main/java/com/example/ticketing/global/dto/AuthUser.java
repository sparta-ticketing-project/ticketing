package com.example.ticketing.global.dto;

import com.example.ticketing.domain.user.enums.UserRole;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthUser {

    private Long userId;
    private UserRole userRole;

    @Builder
    private AuthUser(Long userId, UserRole userRole) {
        this.userId = userId;
        this.userRole = userRole;
    }
}
