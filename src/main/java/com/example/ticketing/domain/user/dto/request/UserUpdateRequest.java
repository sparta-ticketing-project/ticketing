package com.example.ticketing.domain.user.dto.request;

import lombok.Getter;

@Getter
public class UserUpdateRequest {
    private String username;
    private String email;
    private int age;
}
