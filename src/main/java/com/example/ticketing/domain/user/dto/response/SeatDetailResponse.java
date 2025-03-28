package com.example.ticketing.domain.user.dto.response;

import com.example.ticketing.domain.concert.entity.Concert;
import com.example.ticketing.domain.seat.enums.SeatType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeatDetailResponse {

    private Long seatDetailId;

    private Long concertId;

    private SeatType seatType;

    private int price;

    private int totalSeatCount;

    private int availableSeatCount;
}
