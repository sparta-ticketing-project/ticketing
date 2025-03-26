package com.example.ticketing.domain.user.repository;

import com.example.ticketing.domain.user.entity.User;
import com.example.ticketing.global.exception.CustomException;
import com.example.ticketing.global.exception.ExceptionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    default User findByEmailOrElseThrow(String email) {
        return findByEmail(email).orElseThrow(
                () -> new CustomException(ExceptionType.USER_NOT_FOUND)
        );
    }

    default User findByIdOrElseThrow(Long id) {
        return findById(id).orElseThrow(
                () -> new CustomException(ExceptionType.USER_NOT_FOUND)
        );
    }
}
