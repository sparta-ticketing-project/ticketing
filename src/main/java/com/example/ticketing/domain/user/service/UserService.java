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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 유저 조회
    @Transactional(readOnly = true)
    public UserResponse getUser(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionType.USER_NOT_FOUND));
        return new UserResponse(user.getId(), user.getUsername(), user.getPoint(), user.getEmail(), user.getGender(), user.getAge());
    }

    // 유저 수정
    @Transactional
    public UserUpdateResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionType.USER_NOT_FOUND));

        user.update(request.getUsername(), request.getGender(), request.getAge());
        User updatedUser = userRepository.save(user);

        return new UserUpdateResponse(updatedUser.getId(), updatedUser.getUsername(), updatedUser.getGender(), updatedUser.getAge());
    }
}
