package com.example.ticketing.domain.auth.service;

import com.example.ticketing.domain.auth.dto.request.SignUpRequest;
import com.example.ticketing.domain.auth.dto.response.SignUpResponse;
import com.example.ticketing.domain.user.repository.UserRepository;
import com.example.ticketing.domain.user.entity.User;
import com.example.ticketing.global.config.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignUpResponse save(SignUpRequest dto) {
        String encode = passwordEncoder.encode(dto.getPassword());
        User user = User.builder()
                .email(dto.getEmail())
                .password(encode)
                .username(dto.getUsername())
                .userRole(dto.getUserRole())
                .gender(dto.getGender())
                .age(dto.getAge())
                .build();
        userRepository.save(user);

        return SignUpResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .userRole(user.getUserRole())
                .gender(user.getGender())
                .age(user.getAge())
                .point(user.getPoint())
                .createdAt(user.getCreatedAt())
                .modifiedAt(user.getModifiedAt())
                .build();
    }
}
