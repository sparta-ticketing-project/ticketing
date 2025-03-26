package com.example.ticketing.domain.user.dto.request;

import com.example.ticketing.domain.user.enums.Gender;
import lombok.Getter;

@Getter
public class UserUpdateRequest {
    private String username;
    private Gender gender;
    private Integer age;
}
