package com.example.ticketing.domain.auth.repository;

import com.example.ticketing.domain.auth.entity.RefreshToken;
import com.example.ticketing.domain.user.entity.User;
import com.example.ticketing.global.exception.CustomException;
import com.example.ticketing.global.exception.ExceptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUser(User user);

    @Query("select r from RefreshToken r join fetch r.user where r.refreshToken = :refreshToken")
    Optional<RefreshToken> findByRefreshToken(@Param("refreshToken") String refreshToken);

    default RefreshToken findByRefreshTokenOrElseThrow(String refreshToken) {
        return findByRefreshToken(refreshToken).orElseThrow(
                () -> new CustomException(ExceptionType.NOT_FOUND_TOKEN)
        );
    }
}
