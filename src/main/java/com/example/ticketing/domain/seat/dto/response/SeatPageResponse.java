package com.example.ticketing.domain.seat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatPageResponse {
    private int page;
    private int pageSize;
    private long totalCount;
    private int totalPages;
    private List<SeatItemResponse> items;
}