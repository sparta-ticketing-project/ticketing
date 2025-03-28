package com.example.ticketing.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GenderBookingRateResponse {

    private double FemaleBookingRate;

    private double MaleBookingRate;
}
