package com.example.ticketing.domain.user.service;

import com.example.ticketing.domain.user.dto.request.UserUpdateRequest;
import com.example.ticketing.domain.user.dto.response.UserResponse;
import com.example.ticketing.domain.user.dto.response.UserUpdateResponse;
import com.example.ticketing.domain.user.entity.User;
import com.example.ticketing.domain.user.repository.UserRepository;
import com.example.ticketing.global.exception.CustomException;
import com.example.ticketing.global.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 유저 조회
    public UserResponse getUser(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionType.USER_NOT_FOUND));
        return new UserResponse(user.getId(), user.getEmail());
    }

    // 유저 수정
    public UserUpdateResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionType.USER_NOT_FOUND));

        user.update(request.getEmail(), request.getUsername(), request.getAge());
        User updatedUser = userRepository.save(user);

        return new UserUpdateResponse(updatedUser.getId(), updatedUser.getEmail(),updatedUser.getUsername(), request.getAge());
    }
}
