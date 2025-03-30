package com.example.ticketing.domain.seatcount.dto.request;

import com.example.ticketing.domain.ticket.dto.response.TicketResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class SeatDetailCountChange {
    private final Map<Long, Integer> seatDetailIdToCount;

    private SeatDetailCountChange(Map<Long, Integer> seatDetailIdToCount) {
        this.seatDetailIdToCount = seatDetailIdToCount;
    }

    public static SeatDetailCountChange forDecrement(List<TicketResponse> ticketResponses) {
        Map<Long, Integer> countMap = new HashMap<>();

        for (TicketResponse ticket : ticketResponses) {
            Long seatDetailId = ticket.getSeatDetailId();
            countMap.put(seatDetailId, countMap.getOrDefault(seatDetailId, 0) - 1);
        }

        return new SeatDetailCountChange(countMap);
    }

    public static SeatDetailCountChange forIncrement(List<TicketResponse> ticketResponses) {
        Map<Long, Integer> countMap = new HashMap<>();

        for (TicketResponse ticket : ticketResponses) {
            Long seatDetailId = ticket.getSeatDetailId();
            countMap.put(seatDetailId, countMap.getOrDefault(seatDetailId, 0) + 1);
        }
        return new SeatDetailCountChange(countMap);
    }
}
