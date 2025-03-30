package com.example.ticketing.domain.auth.controller;

import com.example.ticketing.domain.auth.dto.request.LoginRequest;
import com.example.ticketing.domain.auth.dto.request.SignUpRequest;
import com.example.ticketing.domain.auth.dto.request.WithDrawRequest;
import com.example.ticketing.domain.auth.dto.response.AccessTokenResponse;
import com.example.ticketing.domain.auth.dto.response.SignUpResponse;
import com.example.ticketing.domain.auth.dto.response.TokenResponse;
import com.example.ticketing.domain.auth.service.AuthService;
import com.example.ticketing.domain.user.enums.Gender;
import com.example.ticketing.domain.user.enums.UserRole;
import com.example.ticketing.domain.user.service.UserService;
import com.example.ticketing.global.auth.JwtUtil;
import com.example.ticketing.global.dto.AuthUser;
import com.example.ticketing.global.exception.CustomException;
import com.example.ticketing.global.exception.ExceptionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    protected EntityManager entityManager;

    protected String accessToken;

    @BeforeEach
    void setUp() {
        accessToken = jwtUtil.createAccessToken(1L, UserRole.ADMIN);
    }

    @DisplayName("ADMIN 회원가입 - 성공")
    @Test
    void signup1() throws Exception {
        SignUpRequest request = SignUpRequest.builder()
                .email("a@a")
                .password("Password123@")
                .username("이름")
                .userRole(UserRole.ADMIN)
                .gender(Gender.NONE)
                .build();

        SignUpResponse signUpResponse = SignUpResponse.builder()
                .id(1L)
                .username(request.getUsername())
                .email(request.getEmail())
                .userRole(request.getUserRole())
                .gender(request.getGender())
                .age(0)
                .point(0)
                .build();
        when(authService.save(any(SignUpRequest.class))).thenReturn(signUpResponse);

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").value("이름"))
                .andExpect(jsonPath("$.email").value("a@a"))
                .andExpect(jsonPath("$.userRole").value(UserRole.ADMIN.name()))
                .andExpect(jsonPath("$.gender").value(Gender.NONE.name()))
                .andExpect(jsonPath("$.age").value(0))
                .andExpect(jsonPath("$.point").value(0));
    }

    @DisplayName("USER 회원가입 - 성공")
    @Test
    void signup2() throws Exception {
        SignUpRequest request = SignUpRequest.builder()
                .email("a@a")
                .password("Password123@")
                .username("이름")
                .userRole(UserRole.USER)
                .gender(Gender.MALE)
                .age(26)
                .build();

        SignUpResponse signUpResponse = SignUpResponse.builder()
                .id(1L)
                .username(request.getUsername())
                .email(request.getEmail())
                .userRole(request.getUserRole())
                .gender(request.getGender())
                .age(request.getAge())
                .point(10000)
                .build();
        when(authService.save(any(SignUpRequest.class))).thenReturn(signUpResponse);

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").value("이름"))
                .andExpect(jsonPath("$.email").value("a@a"))
                .andExpect(jsonPath("$.userRole").value(UserRole.USER.name()))
                .andExpect(jsonPath("$.gender").value(Gender.MALE.name()))
                .andExpect(jsonPath("$.age").value(26))
                .andExpect(jsonPath("$.point").value(10000));
    }

    @DisplayName("회원가입 - userRole에 맞지 않는 gender 값을 입력해서 실패 (400 Bad Request)")
    @Test
    void signup3() throws Exception {
        SignUpRequest request = SignUpRequest.builder()
                .email("a@a")
                .password("Password123@")
                .username("이름")
                .userRole(UserRole.ADMIN)
                .gender(Gender.FEMALE)
                .age(26)
                .build();

        doThrow(new CustomException(ExceptionType.UNSUPPORTED_GENDER))
                .when(authService).save(any(SignUpRequest.class));

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ExceptionType.UNSUPPORTED_GENDER.getMessage()));
    }

    @DisplayName("회원가입 - USER가 나이를 입력하지 않아서 실패 (400 Bad Request)")
    @Test
    void signup4() throws Exception {
        SignUpRequest request = SignUpRequest.builder()
                .email("a@a")
                .password("Password123@")
                .username("이름")
                .userRole(UserRole.USER)
                .gender(Gender.FEMALE)
                .build();

        doThrow(new CustomException(ExceptionType.INVALID_USER_AGE))
                .when(authService).save(any(SignUpRequest.class));

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ExceptionType.INVALID_USER_AGE.getMessage()));
    }

    @DisplayName("회원가입 - 이메일 중복 실패 (409 Conflict)")
    @Test
    void signup5() throws Exception {
        SignUpRequest request = SignUpRequest.builder()
                .email("a@a")
                .password("Password123@")
                .username("이름")
                .userRole(UserRole.USER)
                .gender(Gender.FEMALE)
                .age(26)
                .build();

        doThrow(new CustomException(ExceptionType.DUPLICATE_EMAIL))
                .when(authService).save(any(SignUpRequest.class));

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(ExceptionType.DUPLICATE_EMAIL.getMessage()));
    }

    @DisplayName("로그인 - 성공")
    @Test
    void login1() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("a@a")
                .password("Password123@")
                .build();

        ResponseCookie refreshToken = ResponseCookie.from("refreshToken", "refresh-token")
                .maxAge(0)
                .path("/")
                .build();

        TokenResponse response = new TokenResponse("Bearer jsonToken", refreshToken);

        when(authService.login(any(LoginRequest.class), any(LocalDateTime.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(containsString("Bearer ")));
    }

    @DisplayName("로그인 - 비밀번호 오류(401 UNAUTHORIZED)")
    @Test
    void login2() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("a@a")
                .password("Password123!")
                .build();

        doThrow(new CustomException(ExceptionType.INCORRECT_PASSWORD))
                .when(authService).login(any(LoginRequest.class), any(LocalDateTime.class));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(ExceptionType.INCORRECT_PASSWORD.getMessage()));
    }

    @DisplayName("로그인 - 이미 탈퇴한 회원 로그인(401 UNAUTHORIZED)")
    @Test
    void login3() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("a@a")
                .password("Password123@")
                .build();

        doThrow(new CustomException(ExceptionType.ALREADY_DELETED_USER))
                .when(authService).login(any(LoginRequest.class), any(LocalDateTime.class));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(ExceptionType.ALREADY_DELETED_USER.getMessage()));
    }

    @DisplayName("로그인 - 존재하지 않는 유저(404 NOT_FOUND)")
    @Test
    void login4() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("abc@abc.com")
                .password("Password123@")
                .build();

        doThrow(new CustomException(ExceptionType.USER_NOT_FOUND))
                .when(authService).login(any(LoginRequest.class), any(LocalDateTime.class));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ExceptionType.USER_NOT_FOUND.getMessage()));
    }

    @DisplayName("회원탈퇴 - 성공")
    @Test
    void withdraw1() throws Exception {
        ResponseCookie refreshToken = ResponseCookie.from("refreshToken", "")
                .maxAge(0)
                .path("/")
                .build();

        WithDrawRequest request = WithDrawRequest.builder()
                .password("Password123@")
                .build();

        when(authService.withdraw(any(AuthUser.class), any(WithDrawRequest.class)))
                .thenReturn(refreshToken);

        mockMvc.perform(post("/api/v1/auth/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(AUTHORIZATION, accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(header().exists(SET_COOKIE))
                .andExpect(content().string("회원탈퇴에 성공했습니다."));
    }

    @DisplayName("회원탈퇴 - 비밀번호 오류(401 UNAUTHORIZED)")
    @Test
    void withdraw2() throws Exception {
        WithDrawRequest request = WithDrawRequest.builder()
                .password("Password1234!")
                .build();

        doThrow(new CustomException(ExceptionType.INCORRECT_PASSWORD))
                .when(authService).withdraw(any(AuthUser.class), any(WithDrawRequest.class));

        mockMvc.perform(post("/api/v1/auth/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(AUTHORIZATION, accessToken)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(ExceptionType.INCORRECT_PASSWORD.getMessage()));
    }

    @DisplayName("회원탈퇴 - 이미 탈퇴한 회원(401 UNAUTHORIZED)")
    @Test
    void withdraw3() throws Exception {
        WithDrawRequest request = WithDrawRequest.builder()
                .password("Password1234!")
                .build();

        doThrow(new CustomException(ExceptionType.ALREADY_DELETED_USER))
                .when(authService).withdraw(any(AuthUser.class), any(WithDrawRequest.class));

        mockMvc.perform(post("/api/v1/auth/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(AUTHORIZATION, accessToken)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(ExceptionType.ALREADY_DELETED_USER.getMessage()));
    }

    @DisplayName("회원탈퇴 - 존재하지 않는 유저(404 NOT_FOUND)")
    @Test
    void withdraw4() throws Exception {
        WithDrawRequest request = WithDrawRequest.builder()
                .password("Password1234!")
                .build();

        doThrow(new CustomException(ExceptionType.USER_NOT_FOUND))
                .when(authService).withdraw(any(AuthUser.class), any(WithDrawRequest.class));

        mockMvc.perform(post("/api/v1/auth/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(AUTHORIZATION, accessToken)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ExceptionType.USER_NOT_FOUND.getMessage()));
    }

    @DisplayName("액세스 토큰 재발급 - 성공")
    @Test
    void refresh1() throws Exception {
        String refreshTokenValue = "valid-refresh-token";
        AccessTokenResponse accessTokenResponse = new AccessTokenResponse("Bearer new-access-token");
        Cookie refreshToken = new Cookie("refreshToken", "valid-refresh-token");

        when(authService.refresh(refreshTokenValue))
                .thenReturn(accessTokenResponse);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, accessToken)
                        .cookie(refreshToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(containsString("Bearer ")));
    }
}
