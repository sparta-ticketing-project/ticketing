package com.example.ticketing.domain.seat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConcertResponse {
    private Long concertId;
    private String concertName;
    private LocalDateTime concertDate;
    private LocalDateTime ticketingDate;
    private int maxTicketPerUser;
}