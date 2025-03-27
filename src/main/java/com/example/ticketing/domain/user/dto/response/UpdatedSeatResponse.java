package com.example.ticketing.domain.user.dto.response;

import com.example.ticketing.domain.seat.entity.Seat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class UpdatedSeatResponse {
    List<SeatResponse> updatedAvailableSeats;
    List<SeatResponse> updatedUnavailableSeats;
}
