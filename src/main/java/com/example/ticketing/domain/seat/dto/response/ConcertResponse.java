package com.example.ticketing.domain.seat.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConcertResponse {
    private Long concertId;
    private String concertName;
    private LocalDateTime concertDate;
    private LocalDateTime ticketingDate;
    private int maxTicketPerUser;
}