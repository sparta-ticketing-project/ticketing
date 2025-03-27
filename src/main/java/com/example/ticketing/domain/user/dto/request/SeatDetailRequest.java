package com.example.ticketing.domain.user.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeatDetailRequest {


    @NotBlank(message = "좌석 유형은 필수입니다.")
    private String seatType;

    @Positive(message = "가격은 양수여야 합니다.")
    private int price;

    @Positive(message = "총 좌석 수는 양수여야 합니다.")
    @Min(1)
    @Max(30000)
    private int totalSeatCount;

}
