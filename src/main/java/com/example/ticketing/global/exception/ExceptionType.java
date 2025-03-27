package com.example.ticketing.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionType {

    INVALID_USER_ROLE(HttpStatus.BAD_REQUEST, "유효하지 않은 사용자 유형(UserRole)입니다."),
    INVALID_JWT_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 JWT 토큰입니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INVALID_AUTH_ANNOTATION_USAGE(HttpStatus.BAD_REQUEST, "@Auth와 AuthUser 타입은 함께 사용되어야 합니다."),
    MISSING_JWT_TOKEN(HttpStatus.BAD_REQUEST, "토큰이 요청에 포함되지 않았습니다."),
    REQUEST_VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "요청값 검증에 실패했습니다."),
    REQUIRED_JWT_TOKEN(HttpStatus.BAD_REQUEST, "JWT 토큰이 필요합니다."),
    UNSUPPORTED_JWT_TOKEN(HttpStatus.BAD_REQUEST, "지원되지 않는 JWT 토큰입니다."),

    AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    ALREADY_DELETED_USER(HttpStatus.UNAUTHORIZED, "이미 탈퇴한 사용자입니다."),
    EXPIRED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰입니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "RefreshToken이 만료되었습니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "로그인 정보가 올바르지 않습니다."),
    INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT 서명입니다."),
    INCORRECT_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),

    NO_PERMISSION_ACTION(HttpStatus.FORBIDDEN, "권한이 없는 작업입니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND,  "해당 사용자를 찾을 수 없습니다."),
    NOT_FOUND_TOKEN(HttpStatus.NOT_FOUND, "해당 토큰을 찾을 수 없습니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 문제가 발생했습니다."),

    SAME_AS_OLD_PASSWORD(HttpStatus.CONFLICT, "기존 비밀번호와 새 비밀번호가 같으면 안 됩니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 가입되어 있는 이메일입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ExceptionType(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
