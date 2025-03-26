package com.example.ticketing.domain.user.dto.response;

import com.example.ticketing.domain.user.enums.Gender;
import lombok.Getter;

@Getter
public class UserResponse {

    private final Long id;
    private final String username;
    private final int point;
    private final String email;
    private final Gender gender;
    private final Integer age;

    public UserResponse(Long id, String username, int point, String email, Gender gender, Integer age) {
        this.id = id;
        this.username = username;
        this.point = point;
        this.email = email;
        this.gender = gender;
        this.age = age;
    }


}
