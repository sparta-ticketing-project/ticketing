package com.example.ticketing.domain.auth.dto.response;

import com.example.ticketing.domain.user.enums.Gender;
import com.example.ticketing.domain.user.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SignUpResponse {
    private final Long id;
    private final String username;
    private final String email;
    private final UserRole userRole;
    private final Gender gender;
    private final int age;
    private final int point;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    @Builder
    public SignUpResponse(
            Long id,
            String username,
            String email,
            UserRole userRole,
            Gender gender,
            int age,
            int point,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt
    ) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.userRole = userRole;
        this.gender = gender;
        this.age = age;
        this.point = point;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
