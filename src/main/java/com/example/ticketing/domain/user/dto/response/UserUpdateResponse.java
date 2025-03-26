package com.example.ticketing.domain.user.dto.response;

import com.example.ticketing.domain.user.enums.Gender;
import lombok.Getter;

@Getter
public class UserUpdateResponse {
    private final Long id;
    private final String username;
    private final Gender gender;
    private final Integer age;

    public UserUpdateResponse(Long id, String username, Gender gender, Integer age) {
        this.id = id;
        this.username = username;
        this.gender = gender;
        this.age = age;
    }
}
