package com.example.ticketing.domain.auth.service;

import com.example.ticketing.domain.auth.dto.request.LoginRequest;
import com.example.ticketing.domain.auth.dto.request.SignUpRequest;
import com.example.ticketing.domain.auth.dto.request.WithDrawRequest;
import com.example.ticketing.domain.auth.dto.response.AccessTokenResponse;
import com.example.ticketing.domain.auth.dto.response.SignUpResponse;
import com.example.ticketing.domain.auth.dto.response.TokenResponse;
import com.example.ticketing.domain.auth.entity.RefreshToken;
import com.example.ticketing.domain.auth.repository.RefreshTokenRepository;
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

    private static final Long REFRESH_TOKEN_EXPIRY_DAY = 7 * 24 * 24 * 60L;

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

    @Transactional
    public TokenResponse login(@Valid LoginRequest dto, LocalDateTime expiryDate) {
        User findUser = userRepository.findByEmailOrElseThrow(dto.getEmail());
        if (findUser.isDeleted()) {
            throw new CustomException(ExceptionType.ALREADY_DELETED_USER);
        }

        if (!passwordEncoder.matches(dto.getPassword(), findUser.getPassword())) {
            throw new CustomException(ExceptionType.INVALID_CREDENTIALS);
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
