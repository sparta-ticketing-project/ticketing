package com.example.ticketing.domain.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AccessTokenResponse {
    private final String accessToken;

    @Builder
    public AccessTokenResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public static AccessTokenResponse toDto(TokenResponse response) {
        return new AccessTokenResponse(response.getAccessToken());
    }
}
