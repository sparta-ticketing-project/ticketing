package com.example.ticketing.global.auth;

import com.example.ticketing.domain.user.enums.UserRole;
import com.example.ticketing.global.exception.CustomException;
import com.example.ticketing.global.exception.ExceptionType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

public class JwtUtil {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final long ACCESS_TOKEN_EXPIRATION = 60 * 60 * 1000L;
    private static final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 1000L;

    @Value("${jwt.secret.key}")
    private String secretkey;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretkey.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(Long userId, String email, UserRole userRole) {
        Map<String, String> claims = Map.of(
                "email", email,
                "userRole", userRole.name()
        );

        return BEARER_PREFIX + createToken(userId, claims, ACCESS_TOKEN_EXPIRATION);
    }

    public String createRefreshToken(Long userId) {
        return createToken(userId, Map.of(), REFRESH_TOKEN_EXPIRATION);
    }

    private String createToken(Long userId, Map<String, String> claims, long expiration) {
        Instant now = Instant.now();
        Instant expiry = now.plus(expiration, ChronoUnit.MILLIS);

        return Jwts.builder()
                .subject(userId.toString())
                .claims(claims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(key)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractClaims(token).getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException ex) {
            return true;
        }
    }

    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);
        }

        throw new CustomException(ExceptionType.MISSING_TOKEN);
    }
}
