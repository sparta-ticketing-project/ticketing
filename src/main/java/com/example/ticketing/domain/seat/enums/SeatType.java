package com.example.ticketing.domain.seat.enums;

import com.example.ticketing.domain.concert.enums.ConcertType;
import com.example.ticketing.global.exception.CustomException;
import com.example.ticketing.global.exception.ExceptionType;

import java.util.Arrays;

public enum SeatType {
    R_GRADE,
    S_GRADE,
    A_GRADE;

    public static SeatType fromString(String value) {
        return Arrays.stream(SeatType.values())
                .filter(type -> type.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new CustomException(ExceptionType.INVALID_SEAT_TYPE));
    }
}
