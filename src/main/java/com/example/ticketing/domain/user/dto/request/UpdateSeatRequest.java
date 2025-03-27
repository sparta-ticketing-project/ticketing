package com.example.ticketing.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UpdateSeatRequest {

    private List<Integer> availableSeatList;

    private List<Integer> unavailableSeatList;
}
