package com.example.ticketing.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionType {

    // 400
    REQUEST_VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "요청값 검증에 실패했습니다."),
    INVALID_USER_ROLE(HttpStatus.BAD_REQUEST, "유효하지 않은 사용자 유형(UserRole)입니다."),
    INVALID_JWT_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 JWT 토큰입니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    NO_SUCH_CONCERT_TYPE(HttpStatus.BAD_REQUEST, "존재하지 않는 콘서트 타입입니다."),
    INVALID_AUTH_ANNOTATION_USAGE(HttpStatus.BAD_REQUEST, "@Auth와 AuthUser 타입은 함께 사용되어야 합니다."),
    MISSING_JWT_TOKEN(HttpStatus.BAD_REQUEST, "토큰이 요청에 포함되지 않았습니다."),
    REQUIRED_JWT_TOKEN(HttpStatus.BAD_REQUEST, "JWT 토큰이 필요합니다."),
    UNSUPPORTED_JWT_TOKEN(HttpStatus.BAD_REQUEST, "지원되지 않는 JWT 토큰입니다."),
    UNSUPPORTED_GENDER(HttpStatus.BAD_REQUEST, "선택한 UserRole은 이 성별을 지원하지 않습니다."),
    INVALID_USER_AGE(HttpStatus.BAD_REQUEST, "나이 입력은 필수입니다."),

    // User
    USER_POINT_NOT_ENOUGH(HttpStatus.BAD_REQUEST, "포인트가 부족합니다."),

    // Order
    ORDER_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "유효하지 않은 주문 요청입니다."),
    ORDER_TICKET_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "예매 가능 수량을 초과했습니다."),
    ORDER_CONCERT_MISMATCH(HttpStatus.BAD_REQUEST, "여러 콘서트의 좌석을 하나의 주문에서 예매할 수 없습니다."),
    ORDER_TICKETING_NOT_STARTED(HttpStatus.BAD_REQUEST, "예매가 아직 시작되지 않았습니다."),
    ORDER_SEAT_ALREADY_TAKEN(HttpStatus.BAD_REQUEST, "이미 예매된 좌석입니다."),
    ORDER_POINT_NOT_ENOUGH(HttpStatus.BAD_REQUEST, "포인트가 부족하여 주문이 완료되지 못 했습니다."),

    // Auth
    AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    ALREADY_DELETED_USER(HttpStatus.UNAUTHORIZED, "이미 탈퇴한 사용자입니다."),
    EXPIRED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰입니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "RefreshToken이 만료되었습니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "로그인 정보가 올바르지 않습니다."),
    INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT 서명입니다."),
    INCORRECT_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),

    NO_PERMISSION_ACTION(HttpStatus.FORBIDDEN, "권한이 없는 작업입니다."),

    // Not Found
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,  "해당 사용자를 찾을 수 없습니다."),
    CONCERT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 콘서트를 찾을 수 없습니다."),
    SEAT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 좌석을 찾을 수 없습니다."),
    NOT_FOUND_TOKEN(HttpStatus.NOT_FOUND, "해당 토큰을 찾을 수 없습니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 주문을 찾을 수 없습니다."),
    SEAT_DETAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 SeatDeatail을 찾을 수 없습니다."),
    SEAT_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 SeatType을 찾을 수 없습니다."),

    INVALID_CONCERT_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 콘서트 타입입니다."),
    INVALID_SEAT_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 좌석 타입입니다."),
    INVALID_TICKETING_DATE(HttpStatus.BAD_REQUEST, "콘서트 예매시간은 공연 시간보다 이전이어야 합니다."),

    CONCERT_DELETION_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "사용가능한 티켓이 존재하여 콘서트 삭제 불가합니다."),
    CONCERT_MODIFICATION_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "예매시간 이후에 수정할 수 없습니다."),
    SEAT_UPDATE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "본인이 생성한 콘서트의 좌석만 수정할 수 있습니다."),

    // Server Error
    INVALID_PAGE_NUMBER(HttpStatus.BAD_REQUEST, "페이지 번호는 1 이상이어야 합니다."),
    INVALID_PAGE_SIZE(HttpStatus.BAD_REQUEST, "페이지 크기는 1 이상 100 이하여야 합니다."),

    CONCERT_SEAT_MISMATCH(HttpStatus.BAD_REQUEST, "해당 콘서트의 좌석이 아닙니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 문제가 발생했습니다."),

    SAME_AS_OLD_PASSWORD(HttpStatus.CONFLICT, "기존 비밀번호와 새 비밀번호가 같으면 안 됩니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 가입되어 있는 이메일입니다."),
    RESOURCE_LOCKED(HttpStatus.CONFLICT, "다른 사용자가 해당 자원을 사용 중입니다."),
    LOCK_OWNERSHIP_LOST(HttpStatus.CONFLICT, "요청이 만료되었거나 다른 사용자에 의해 처리 중입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ExceptionType(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
