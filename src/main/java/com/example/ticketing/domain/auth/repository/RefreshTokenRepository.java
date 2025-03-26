package com.example.ticketing.domain.auth.repository;

import com.example.ticketing.domain.auth.entity.RefreshToken;
import com.example.ticketing.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUser(User user);
}
