package com.example.ticketing.domain.user.enums;

import com.example.ticketing.global.exception.CustomException;
import com.example.ticketing.global.exception.ExceptionType;

import java.util.Arrays;

public enum UserRole {
    USER, ADMIN;

    public static UserRole of(String type) {
        return Arrays.stream(UserRole.values())
                .filter(t -> t.name().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new CustomException(ExceptionType.INVALID_USER_ROLE));
    }
}
