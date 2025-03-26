package com.example.ticketing.domain.user.entity;

import com.example.ticketing.domain.user.enums.Gender;
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

    @Column(unique = true)
    private String email;

    private String password;

    private String username;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Integer age;

    private int point;

    private boolean deleted;

    @Builder
    public User(String email, String password, String username, UserRole userRole, Gender gender, int age) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.userRole = userRole;
        this.gender = gender;
        this.age = age;
        this.point = 10000;
        this.deleted = false;
    }

    public void deleteUser() {
        this.deleted = true;
    }
}
