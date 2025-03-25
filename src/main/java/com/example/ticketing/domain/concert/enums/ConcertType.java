package com.example.ticketing.domain.concert.enums;

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
}
