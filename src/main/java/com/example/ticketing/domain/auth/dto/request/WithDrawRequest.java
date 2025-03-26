package com.example.ticketing.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WithDrawRequest {
    @NotBlank(message = "비밀번호 입력은 필수입니다.")
    private String password;

    @Builder
    public WithDrawRequest(String password) {
        this.password = password;
    }
}
