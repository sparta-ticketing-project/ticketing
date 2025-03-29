package com.example.ticketing.domain.seat.service;

import com.example.ticketing.domain.concert.entity.Concert;
import com.example.ticketing.domain.concert.repository.ConcertRepository;
import com.example.ticketing.domain.seat.dto.response.SeatOneResponse;
import com.example.ticketing.domain.seat.dto.response.SeatPageResponse;
import com.example.ticketing.domain.seat.entity.Seat;
import com.example.ticketing.domain.seat.entity.SeatDetail;
import com.example.ticketing.domain.seat.enums.SeatType;
import com.example.ticketing.domain.seat.repository.SeatRepository;
import com.example.ticketing.global.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class SeatServiceTest {


    @Mock
    private SeatRepository seatRepository;

    @Mock
    private ConcertRepository concertRepository;

    @InjectMocks
    private SeatService seatService;

    private Concert concert;
    private SeatDetail seatDetail;
    private Seat seat1;
    private Seat seat2;

    @BeforeEach
    void setUp() {
        concert = Concert.builder()
                .concertName("Test Concert")
                .concertDate(LocalDateTime.now().plusDays(7))
                .ticketingDate(LocalDateTime.now())
                .maxTicketPerUser(4)
                .isDeleted(false)
                .build();
        ReflectionTestUtils.setField(concert, "id", 1L);

        seatDetail = SeatDetail.builder()
                .concert(concert)
                .seatType(SeatType.R_GRADE)
                .price(100000)
                .totalSeatCount(100)
                .availableSeatCount(100)
                .build();
        ReflectionTestUtils.setField(seatDetail, "id", 1L);
        ReflectionTestUtils.setField(concert, "isDeleted", false);

        seat1 = Seat.builder()
                .concert(concert)
                .seatDetail(seatDetail)
                .isAvailable(true)
                .seatNumber(1)
                .build();
        ReflectionTestUtils.setField(seat1, "id", 1L);

        seat2 = Seat.builder()
                .concert(concert)
                .seatDetail(seatDetail)
                .isAvailable(true)
                .seatNumber(2)
                .build();
        ReflectionTestUtils.setField(seat2, "id", 2L);
    }

    @Test
    void 좌석_전체_조회를_좌석_상세_ID_파라미터를_입력하고_성공한다() {
        Page<Seat> seatPage = new PageImpl<>(Arrays.asList(seat1, seat2));
        when(concertRepository.findById(1L)).thenReturn(Optional.of(concert));
        when(seatRepository.findByConcertIdAndSeatDetailIdAndIsAvailableTrue(eq(1L), eq(1L), any(PageRequest.class))).thenReturn(seatPage);

        SeatPageResponse response = seatService.getSeats(1L, 1, 10, 1L);

        assertNotNull(response);
        assertEquals(2, response.getSeatPageDataResponses().size());
        assertEquals(1, response.getSeatPageDataResponses().get(0).getSeatNumber());
    }

    @Test
    void 좌석_전체_조회를_좌석_상세_ID_파라미터_없이_성공한다() {
        Page<Seat> seatPage = new PageImpl<>(Arrays.asList(seat1, seat2));
        when(concertRepository.findById(1L)).thenReturn(Optional.of(concert));
        when(seatRepository.findByConcertIdAndIsAvailableTrue(eq(1L), any(PageRequest.class))).thenReturn(seatPage);

        SeatPageResponse response = seatService.getSeats(1L, 1, 10, null);

        assertNotNull(response);
        assertEquals(2, response.getSeatPageDataResponses().size());
        assertEquals(1, response.getSeatPageDataResponses().get(0).getSeatNumber());
    }

    @Test
    void 좌석_전체_조회_시_콘서트_ID가_없는_경우_예외가_발생한다() {
        when(concertRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> seatService.getSeats(1L, 1, 10, null));
    }

    @Test
    void 좌석_전체_조회_시_삭제된_콘서트인_경우_예외가_발생한다() {
        ReflectionTestUtils.setField(concert, "isDeleted", true); // deleted 상태로 변경
        when(concertRepository.findById(1L)).thenReturn(Optional.of(concert));

        assertThrows(CustomException.class, () -> seatService.getSeats(1L, 1, 10, null));
    }

    @Test
    void 좌석_단건_조회를_성공한다() {
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat1));

        SeatOneResponse response = seatService.getSeat(1L, 1L);

        assertNotNull(response);
        assertEquals(1L, response.getSeatId());
        assertEquals(SeatType.R_GRADE, response.getSeatType());
    }

    @Test
    void 좌석_단건_조회_시_좌석_ID가_없으면_예외가_발생한다() {
        when(seatRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> seatService.getSeat(1L, 1L));
    }

    @Test
    void 좌석_단건_조회_시_콘서트_ID_없으면_예외_발생한다() {
        ReflectionTestUtils.setField(seat1.getConcert(), "isDeleted", true);
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat1));

        assertThrows(CustomException.class, () -> seatService.getSeat(1L, 1L));
    }

    @Test
    void 좌석_단건_조회_시_콘서트_ID에_해당하는_좌석이_아니면_예외_발생한다() {
        Concert differentConcert = Concert.builder()
                .isDeleted(false)
                .build();
        ReflectionTestUtils.setField(differentConcert, "id", 2L);

        Seat seat3 = Seat.builder().concert(differentConcert).build();
        ReflectionTestUtils.setField(seat3, "id", 1L);
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat3));

        assertThrows(CustomException.class, () -> seatService.getSeat(1L, 1L));
    }

    @Test
    void 좌석_단건_조회_시_좌석_상세_정보가_없으면_예외_발생한다() {
        ReflectionTestUtils.setField(seat1, "seatDetail", null); // seatDetail 필드를 null로 설정
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat1));

        assertThrows(CustomException.class, () -> seatService.getSeat(1L, 1L));
    }

}