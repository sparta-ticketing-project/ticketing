package com.example.ticketing.domain.seat.controller;

import com.example.ticketing.domain.seat.dto.response.ConcertResponse;
import com.example.ticketing.domain.seat.dto.response.SeatOneResponse;
import com.example.ticketing.domain.seat.dto.response.SeatPageDataResponse;
import com.example.ticketing.domain.seat.dto.response.SeatPageResponse;
import com.example.ticketing.domain.seat.enums.SeatType;
import com.example.ticketing.domain.seat.service.SeatService;
import com.example.ticketing.domain.user.enums.UserRole;
import com.example.ticketing.global.auth.AuthUserArgumentResolver;
import com.example.ticketing.global.dto.AuthUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
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
    private final Long userId = 20L;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(seatController)
                .setCustomArgumentResolvers(new AuthUserArgumentResolver())
                .build();
    }

    private class AuthUserArgumentResolver implements HandlerMethodArgumentResolver {
        @Override
        public boolean supportsParameter(org.springframework.core.MethodParameter parameter) {
            return parameter.getParameterType().equals(AuthUser.class);
        }

        @Override
        public Object resolveArgument(org.springframework.core.MethodParameter parameter, org.springframework.web.method.support.ModelAndViewContainer mavContainer, org.springframework.web.context.request.NativeWebRequest webRequest, org.springframework.web.bind.support.WebDataBinderFactory binderFactory) throws Exception {
            Constructor<AuthUser> constructor = AuthUser.class.getDeclaredConstructor(Long.class, UserRole.class);
            constructor.setAccessible(true);
            return constructor.newInstance(userId, UserRole.USER);
        }
    }

    @Test
    void 좌석_전체_조회_성공한다() throws Exception {
        SeatPageResponse response = new SeatPageResponse(
                new ConcertResponse(concertId, "Concert Name", LocalDateTime.now(), LocalDateTime.now(), 4),
                List.of(new SeatPageDataResponse(1L, true, 101, SeatType.R_GRADE, 100000))
        );

        when(seatService.getSeats(eq(userId), eq(concertId), anyInt(), anyInt(), any())).thenReturn(response);

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

        when(seatService.getSeat(eq(userId), eq(concertId), eq(seatId))).thenReturn(response);

        mockMvc.perform(get("/api/v1/concerts/{concertId}/seats/{seatId}", concertId, seatId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seatId").value(seatId))
                .andExpect(jsonPath("$.concertId").value(concertId));
    }
}
