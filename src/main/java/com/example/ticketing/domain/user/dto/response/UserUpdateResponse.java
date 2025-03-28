package com.example.ticketing.domain.user.dto.response;

import lombok.Getter;

@Getter
public class UserUpdateResponse {
    private final Long id;
    private final String username;
    private final String email;
    private final int age;

    public UserUpdateResponse(Long id, String username, String email, int age) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.age = age;
    }
}
