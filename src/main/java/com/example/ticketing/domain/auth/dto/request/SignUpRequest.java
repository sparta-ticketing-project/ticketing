package com.example.ticketing.domain.auth.dto.request;

import com.example.ticketing.domain.user.enums.Gender;
import com.example.ticketing.domain.user.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignUpRequest {

    @Email(message = "올바르지 않은 이메일 형식입니다.")
    @NotBlank(message = "이메일 입력은 필수입니다.")
    private String email;

    @NotBlank(message = "비밀번호 입력은 필수입니다.")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,}$",
            message = "비밀번호는 최소 8자 이상이며, 영문, 숫자, 특수문자를 포함해야 합니다."
    )
    private String password;

    @NotBlank(message = "유저이름 입력은 필수입니다.")
    private String username;

    @NotNull(message = "유저역할 입력은 필수입니다.")
    private UserRole userRole;

    @NotNull(message = "성별 입력은 필수입니다.")
    private Gender gender;

    private Integer age;

    @Builder
    public SignUpRequest(String email, String password, String username, UserRole userRole, Gender gender, Integer age) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.userRole = userRole;
        this.gender = gender;
        this.age = age;
    }
}
