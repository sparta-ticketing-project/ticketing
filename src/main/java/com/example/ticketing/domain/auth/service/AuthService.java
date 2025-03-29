package com.example.ticketing.domain.auth.service;

import com.example.ticketing.domain.auth.dto.request.LoginRequest;
import com.example.ticketing.domain.auth.dto.request.SignUpRequest;
import com.example.ticketing.domain.auth.dto.request.UpdatePasswordRequest;
import com.example.ticketing.domain.auth.dto.request.WithDrawRequest;
import com.example.ticketing.domain.auth.dto.response.AccessTokenResponse;
import com.example.ticketing.domain.auth.dto.response.SignUpResponse;
import com.example.ticketing.domain.auth.dto.response.TokenResponse;
import com.example.ticketing.domain.auth.entity.RefreshToken;
import com.example.ticketing.domain.auth.repository.RefreshTokenRepository;
import com.example.ticketing.domain.user.enums.Gender;
import com.example.ticketing.domain.user.enums.UserRole;
import com.example.ticketing.domain.user.repository.UserRepository;
import com.example.ticketing.domain.user.entity.User;
import com.example.ticketing.global.auth.JwtUtil;
import com.example.ticketing.global.config.PasswordEncoder;
import com.example.ticketing.global.dto.AuthUser;
import com.example.ticketing.global.exception.CustomException;
import com.example.ticketing.global.exception.ExceptionType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private static final Long REFRESH_TOKEN_EXPIRY_DAY = 7 * 24 * 60 * 60 * 1000L;

    @Transactional
    public SignUpResponse save(SignUpRequest dto) {
        userRepository.findByEmail(dto.getEmail())
                .ifPresent(user -> {
                    throw new CustomException(ExceptionType.DUPLICATE_EMAIL);
                });

        if (dto.getUserRole().equals(UserRole.ADMIN)) {
            if (dto.getGender().equals(Gender.MALE) || dto.getGender().equals(Gender.FEMALE)) {
                throw new CustomException(ExceptionType.UNSUPPORTED_GENDER);
            }
            User user = saveAdmin(dto);

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

        if (dto.getUserRole().equals(UserRole.USER)) {
            if (dto.getGender().equals(Gender.NONE)) {
                throw new CustomException(ExceptionType.UNSUPPORTED_GENDER);
            }
            if (dto.getAge() == null) {
                throw new CustomException(ExceptionType.INVALID_USER_AGE);
            }
            User user = saveUser(dto);

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
        throw new CustomException(ExceptionType.INVALID_REQUEST);
    }

    @Transactional
    public User saveUser(SignUpRequest dto) {
        String encode = passwordEncoder.encode(dto.getPassword());
        User user = User.builder()
                .email(dto.getEmail())
                .password(encode)
                .username(dto.getUsername())
                .userRole(dto.getUserRole())
                .gender(dto.getGender())
                .age(dto.getAge())
                .point(10000)
                .build();
        userRepository.save(user);
        return user;
    }

    @Transactional
    public User saveAdmin(SignUpRequest dto) {
        String encode = passwordEncoder.encode(dto.getPassword());
        User user = User.builder()
                .email(dto.getEmail())
                .password(encode)
                .username(dto.getUsername())
                .userRole(dto.getUserRole())
                .gender(Gender.NONE)
                .age(0)
                .point(0)
                .build();
        userRepository.save(user);
        return user;
    }

    @Transactional
    public TokenResponse login(@Valid LoginRequest dto, LocalDateTime expiryDate) {
        User findUser = userRepository.findByEmailOrElseThrow(dto.getEmail());
        if (findUser.isDeleted()) {
            throw new CustomException(ExceptionType.ALREADY_DELETED_USER);
        }

        if (!passwordEncoder.matches(dto.getPassword(), findUser.getPassword())) {
            throw new CustomException(ExceptionType.INCORRECT_PASSWORD);
        }

        String accessToken = jwtUtil.createAccessToken(findUser.getId(), findUser.getUserRole());
        String refreshToken = jwtUtil.createRefreshToken(findUser.getId());

        refreshTokenRepository.findByUser(findUser)
                .ifPresentOrElse(
                        token -> token.updateToken(refreshToken, expiryDate),
                        () ->refreshTokenRepository.save(
                                RefreshToken.builder()
                                        .user(findUser)
                                        .refreshToken(refreshToken)
                                        .expiryDate(expiryDate)
                                        .build()
                        )
                );

        return new TokenResponse(accessToken, createRefreshTokenCookie(refreshToken, REFRESH_TOKEN_EXPIRY_DAY));
    }

    @Transactional
    public void updatePassword(AuthUser authUser, UpdatePasswordRequest dto) {
        User findUser = userRepository.findByIdOrElseThrow(authUser.getUserId());
        if (dto.getOldPassword().equals(dto.getNewPassword())) {
            throw new CustomException(ExceptionType.SAME_AS_OLD_PASSWORD);
        }

        if (!passwordEncoder.matches(dto.getOldPassword(), findUser.getPassword())) {
            throw new CustomException(ExceptionType.INCORRECT_PASSWORD);
        }

        findUser.updatePassword(passwordEncoder.encode(dto.getNewPassword()));
    }

    @Transactional
    public ResponseCookie withdraw(AuthUser authUser, WithDrawRequest dto) {
        User findUser = userRepository.findByIdOrElseThrow(authUser.getUserId());
        if (!passwordEncoder.matches(dto.getPassword(), findUser.getPassword())) {
            throw new CustomException(ExceptionType.INVALID_CREDENTIALS);
        }

        if (findUser.isDeleted()) {
            throw new CustomException(ExceptionType.ALREADY_DELETED_USER);
        }

        findUser.deleteUser();

        refreshTokenRepository.findByUser(findUser)
                .ifPresent(refreshTokenRepository::delete);

        return createRefreshTokenCookie("", 0);
    }

    @Transactional
    public AccessTokenResponse refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank() || jwtUtil.isTokenExpired(refreshToken)) {
            throw new CustomException(ExceptionType.EXPIRED_REFRESH_TOKEN);
        }

        RefreshToken savedToken = refreshTokenRepository.findByRefreshTokenOrElseThrow(refreshToken);

        User user = savedToken.getUser();
        String newAccessToken = jwtUtil.createAccessToken(user.getId(), user.getUserRole());

        return new AccessTokenResponse(newAccessToken);
    }

    private ResponseCookie createRefreshTokenCookie(String refreshToken, long maxAgeSeconds) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .maxAge(maxAgeSeconds)
                .path("/")
                .build();
    }
}
