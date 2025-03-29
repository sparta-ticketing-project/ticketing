package com.example.ticketing.domain.user.entity;

import com.example.ticketing.domain.user.enums.Gender;
import com.example.ticketing.domain.user.enums.UserRole;
import com.example.ticketing.global.entity.BaseTimeEntity;
import com.example.ticketing.global.exception.CustomException;
import com.example.ticketing.global.exception.ExceptionType;
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

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private int point;

    @Column(nullable = false)
    private boolean deleted;

    @Builder
    public User(String email, String password, String username, UserRole userRole, Gender gender, Integer age, int point) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.userRole = userRole;
        this.gender = gender;
        this.age = age;
        this.point = point;
        this.deleted = false;
    }

    public void update(String username, Gender gender, Integer age) {
        this.username = username;
        this.gender = gender;
        this.age = age;
    }

    public void deleteUser() {
        this.deleted = true;
        this.email = null;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void deductPoint(int amount) {
        if (this.point < amount) {
            throw new CustomException(ExceptionType.USER_POINT_NOT_ENOUGH);
        }

        this.point -= amount;
    }

    public void addPoint(int amount) {
        this.point += amount;
    }
}
