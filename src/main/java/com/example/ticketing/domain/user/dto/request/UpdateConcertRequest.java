package com.example.ticketing.domain.user.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class UpdateConcertRequest {

    @Size(min = 1, max = 100, message = "콘서트 이름은 1자에서 100자 사이여야 합니다.")
    private String concertName;

    @Future(message = "콘서트 날짜는 현재 시간 이후여야 합니다.")
    private LocalDateTime concertDate;

    @Future(message = "티켓팅 날짜는 현재 시간 이후여야 합니다.")
    private LocalDateTime ticketingDate;

    private String concertType;

    @Positive(message = "사용자당 최대 티켓 수는 양수여야 합니다.")
    private int maxTicketPerUser;

    @Valid
    private List<UpdateConcertSeatDetailRequest> seatDetail;


}
