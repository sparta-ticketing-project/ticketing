package com.example.ticketing.domain.concert.enums;

import com.example.ticketing.global.exception.CustomException;
import com.example.ticketing.global.exception.ExceptionType;

import java.util.Arrays;

public enum ConcertType {
    MUSICAL("뮤지컬"),
    SINGER("가수 콘서트"),
    ORCHESTRA("오케스트라"),
    PLAY("연극"),
    ETC("기타");

    private final String description;

    ConcertType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static ConcertType of(String name){
        return Arrays.stream(values())
                .filter(concertType -> concertType.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() ->new CustomException(ExceptionType.NO_SUCH_CONCERT_TYPE));
    }

    public static ConcertType fromString(String value) {

        return Arrays.stream(ConcertType.values())
                .filter(type -> type.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new CustomException(ExceptionType.INVALID_CONCERT_TYPE));
    }
}
