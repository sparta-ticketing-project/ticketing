package com.example.ticketing.domain.auth.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseCookie;

@Getter
public class TokenResponse {
    private final String accessToken;
    private final ResponseCookie refreshToken;

    @Builder
    public TokenResponse(String accessToken, ResponseCookie refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
