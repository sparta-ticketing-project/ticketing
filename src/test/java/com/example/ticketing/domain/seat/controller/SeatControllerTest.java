package com.example.ticketing.domain.seat.controller;

import com.example.ticketing.domain.seat.dto.response.ConcertResponse;
import com.example.ticketing.domain.seat.dto.response.SeatOneResponse;
import com.example.ticketing.domain.seat.dto.response.SeatPageDataResponse;
import com.example.ticketing.domain.seat.dto.response.SeatPageResponse;
import com.example.ticketing.domain.seat.enums.SeatType;
import com.example.ticketing.domain.seat.service.SeatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SeatControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SeatService seatService;

    @InjectMocks
    private SeatController seatController;

    private final Long concertId = 1L;
    private final Long seatId = 10L;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(seatController).build();
    }

    @Test
    void 좌석_전체_조회_성공한다() throws Exception {
        SeatPageResponse response = new SeatPageResponse(
                new ConcertResponse(concertId, "Concert Name", LocalDateTime.now(), LocalDateTime.now(), 4),
                List.of(new SeatPageDataResponse(1L, true, 101, SeatType.R_GRADE, 100000))
        );

        when(seatService.getSeats(eq(concertId), anyInt(), anyInt(), any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/concerts/{concertId}/seats", concertId)
                        .param("page", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.concert.concertId").value(concertId))
                .andExpect(jsonPath("$.seatPageDataResponses[0].seatId").value(1L));
    }

    @Test
    void 좌석_단건_조회_성공한다() throws Exception {
        SeatOneResponse response = new SeatOneResponse(seatId, concertId, 2L, SeatType.S_GRADE, 100000, true, 101);

        when(seatService.getSeat(concertId, seatId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/concerts/{concertId}/seats/{seatId}", concertId, seatId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seatId").value(seatId))
                .andExpect(jsonPath("$.concertId").value(concertId));
    }
}

