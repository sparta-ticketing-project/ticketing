package com.example.ticketing.domain.user.dto.response;

import com.example.ticketing.domain.seat.entity.SeatDetail;
import com.example.ticketing.domain.user.dto.request.SeatDetailRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ConcertResponse {

    private Long concertId;

    private Long userId;

    private String concertName;

    private LocalDateTime concertDate;

    private LocalDateTime ticketingDate;

    private String concertType;

    private int maxTicketPerUser;

    private int totalSeatCount;

    private int availableSeatCount;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    private Boolean isDeleted;

    private List<SeatDetailResponse> seatDetail;

}
