package com.example.ticketing.domain.user.service;

import com.example.ticketing.domain.user.dto.request.UserUpdateRequest;
import com.example.ticketing.domain.user.dto.response.UserResponse;
import com.example.ticketing.domain.user.dto.response.UserUpdateResponse;
import com.example.ticketing.domain.user.entity.User;
import com.example.ticketing.domain.user.enums.Gender;
import com.example.ticketing.domain.user.enums.UserRole;
import com.example.ticketing.domain.user.repository.UserRepository;
import com.example.ticketing.global.dto.AuthUser;
import com.example.ticketing.global.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private Long userId;
    private User user;
    private AuthUser authUser;

    @BeforeEach
    void setUp() {
        userId = 1L;
        user = User.builder()
                .email("test@example.com")
                .username("tester")
                .userRole(UserRole.USER)
                .gender(Gender.MALE)
                .age(25)
                .point(10000)
                .password("encodedPassword")
                .deleted(false)
                .build();

        ReflectionTestUtils.setField(user, "id", userId);
        authUser = AuthUser.builder()
                .userId(userId)
                .userRole(UserRole.USER)
                .build();
    }

    @Test
    void 유저_단건_조회_성공한다() {
        // given
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        UserResponse response = userService.getUser(userId, authUser);

        // then
        assertNotNull(response);
        assertEquals(userId, response.getId());
        assertEquals("tester", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
        assertEquals(Gender.MALE, response.getGender());
        assertEquals(25, response.getAge());
        assertEquals(10000, response.getPoint());
    }

    @Test
    void 유저_단건_조회_시_유저가_존재하지_않아_예외가_발생한다() {
        // given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(CustomException.class, () -> userService.getUser(userId, authUser));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void 유저_단건_조회_시_권한이_없어_예외가_발생한다() {
        // given
        AuthUser otherUser = AuthUser.builder()
                .userId(2L)
                .userRole(UserRole.ADMIN)
                .build();

        // when & then
        assertThrows(CustomException.class, () -> userService.getUser(userId, otherUser));
    }

    @Test
    void 유저_정보_수정을_성공한다() {
        // given
        UserUpdateRequest request = new UserUpdateRequest("newTester", Gender.FEMALE, 30);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // when
        UserUpdateResponse response = userService.updateUser(userId, request, authUser);

        // then
        assertNotNull(response);
        assertEquals(userId, response.getId());
        assertEquals("newTester", response.getUsername());
        assertEquals(Gender.FEMALE, response.getGender());
        assertEquals(30, response.getAge());
    }

    @Test
    void 유저_정보_수정_시_유저가_존재하지_않아_예외가_발생한다() {
        // given
        UserUpdateRequest request = new UserUpdateRequest("newTester", Gender.FEMALE, 30);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(CustomException.class, () -> userService.updateUser(userId, request, authUser));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void 유저_정보_수정_시_권한이_없어_예외가_발생한다() {
        // given
        UserUpdateRequest request = new UserUpdateRequest("newTester", Gender.FEMALE, 30);
        AuthUser otherUser = AuthUser.builder()
                .userId(2L)
                .userRole(UserRole.ADMIN)
                .build();

        // when & then
        assertThrows(CustomException.class, () -> userService.updateUser(userId, request, otherUser));
    }
}