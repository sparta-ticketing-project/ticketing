package com.example.ticketing.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionType {

    REQUEST_VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "요청값 검증에 실패했습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

    AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "로그인 정보가 올바르지 않습니다."),

    NO_PERMISSION_ACTION(HttpStatus.FORBIDDEN, "권한이 없는 작업입니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND,  "해당 사용자를 찾을 수 없습니다."),
    CONCERT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 콘서트를 찾을 수 없습니다."),
    SEAT_DETAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 SeatDeatail을 찾을 수 없습니다."),
    SEAT_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 SeatType을 찾을 수 없습니다."),
    SEAT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 Seat를 찾을 수 없습니다"),

    INVALID_TICKETING_DATE(HttpStatus.BAD_REQUEST, "콘서트 예매시간은 공연 시간보다 이전이어야 합니다."),
    CONCERT_MODIFICATION_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "콘서트 예매시간 이후에 수정할 수 없습니다."),
    INVALID_SEAT_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 좌석 타입입니다."),
    CONCERT_DELETION_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "사용가능한 티켓이 존재하여 콘서트 삭제 불가합니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 문제가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ExceptionType(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
