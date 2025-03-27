package com.example.ticketing.domain.seat.dto.response;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatPageResponse {
    private int page;
    private int pageSize;
    private long totalCount;
    private int totalPages;
    private List<SeatItemResponse> items;
}