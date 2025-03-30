package com.example.ticketing.domain.user.controller;

import com.example.ticketing.domain.user.dto.request.UserUpdateRequest;
import com.example.ticketing.domain.user.dto.response.UserResponse;
import com.example.ticketing.domain.user.dto.response.UserUpdateResponse;
import com.example.ticketing.domain.user.enums.Gender;
import com.example.ticketing.domain.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Constructor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    private Long userId;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        Constructor<Long> constructor = Long.class.getDeclaredConstructor(long.class);
        constructor.setAccessible(true);
        userId = constructor.newInstance(1L);
    }

    @Test
    void 유저_단건_조회를_성공한다() throws Exception {
        UserResponse userResponse = new UserResponse(userId, "testUser", 10000, "test@example.com", Gender.MALE, 30);

        when(userService.getUser(eq(userId), any())).thenReturn(userResponse);

        mockMvc.perform(get("/api/v1/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.username").value("testUser"));
    }

    @Test
    void 유저_정보_수정을_성공한다() throws Exception {
        UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
                .username("updatedUser")
                .gender(Gender.FEMALE)
                .age(25)
                .build();
        UserUpdateResponse userUpdateResponse = new UserUpdateResponse(userId, "updatedUser", Gender.FEMALE, 25);

        when(userService.updateUser(eq(userId), any(UserUpdateRequest.class), any())).thenReturn(userUpdateResponse);

        mockMvc.perform(put("/api/v1/update/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updatedUser"))
                .andExpect(jsonPath("$.gender").value("FEMALE"));
    }
}
