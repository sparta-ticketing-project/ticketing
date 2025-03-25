package com.example.ticketing.domain.user.entity;

import com.example.ticketing.domain.user.enums.UserRole;
import com.example.ticketing.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String username;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Builder
    public User(String email, String password, String username, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.userRole = userRole;
    }
}
