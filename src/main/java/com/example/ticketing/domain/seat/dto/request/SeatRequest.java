package com.example.ticketing.domain.seat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatRequest {
    private Long concertId;
    private int page;
    private int pageSize;
}