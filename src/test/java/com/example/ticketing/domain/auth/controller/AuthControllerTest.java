package com.example.ticketing.domain.auth.controller;

import com.example.ticketing.domain.auth.dto.request.SignUpRequest;
import com.example.ticketing.domain.auth.service.AuthService;
import com.example.ticketing.domain.user.enums.Gender;
import com.example.ticketing.domain.user.enums.UserRole;
import com.example.ticketing.global.exception.CustomException;
import com.example.ticketing.global.exception.ExceptionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Mock 객체 초기화
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
                .email("a1@a")
                .password("Password123@")
                .username("이름")
                .userRole(UserRole.USER)
                .gender(Gender.MALE)
                .age(26)
                .build();

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").value("이름"))
                .andExpect(jsonPath("$.email").value("a1@a"))
                .andExpect(jsonPath("$.userRole").value(UserRole.USER.name()))
                .andExpect(jsonPath("$.gender").value(Gender.MALE.name()))
                .andExpect(jsonPath("$.age").value(26))
                .andExpect(jsonPath("$.point").value(10000));
    }

    @DisplayName("회원가입 - userRole에 맞지 않는 gender 값을 입력해서 실패 (400 Bad Request)")
    @Test
    void signup3() throws Exception {
        SignUpRequest request = SignUpRequest.builder()
                .email("a2@a")
                .password("Password123@")
                .username("이름")
                .userRole(UserRole.ADMIN)
                .gender(Gender.FEMALE)
                .age(26)
                .build();

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
                .email("a3@a")
                .password("Password123@")
                .username("이름")
                .userRole(UserRole.USER)
                .gender(Gender.FEMALE)
                .build();

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
                .email("a4@a")
                .password("Password123@")
                .username("이름")
                .userRole(UserRole.USER)
                .gender(Gender.FEMALE)
                .age(26)
                .build();

        SignUpRequest request2 = SignUpRequest.builder()
                .email("a4@a")
                .password("Password123@")
                .username("이름")
                .userRole(UserRole.USER)
                .gender(Gender.FEMALE)
                .age(26)
                .build();

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        doThrow(new CustomException(ExceptionType.DUPLICATE_EMAIL))
                .when(authService).save(any(SignUpRequest.class));

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(ExceptionType.DUPLICATE_EMAIL.getMessage()));
    }
}
