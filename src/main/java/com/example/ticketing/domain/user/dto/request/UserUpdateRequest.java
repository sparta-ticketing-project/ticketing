package com.example.ticketing.domain.user.dto.request;

import com.example.ticketing.domain.user.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserUpdateRequest {
    @NotBlank
    private String username;
    @NotNull
    private Gender gender;
    @NotNull
    private Integer age;

    @Builder
    public UserUpdateRequest(String username, Gender gender, Integer age) {
        this.username = username;
        this.gender = gender;
        this.age = age;
    }
}
