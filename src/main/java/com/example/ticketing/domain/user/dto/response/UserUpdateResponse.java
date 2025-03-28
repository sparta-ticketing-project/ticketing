package com.example.ticketing.domain.user.dto.response;

import com.example.ticketing.domain.user.enums.Gender;
import lombok.Getter;

@Getter
public class UserUpdateResponse {
    private final Long id;
    private final String username;
    private final Integer age;
    private final Gender gender;

    public UserUpdateResponse(Long id, String username, Integer age, Gender gender) {
        this.id = id;
        this.username = username;
        this.age = age;
        this.gender = gender;
    }
}
