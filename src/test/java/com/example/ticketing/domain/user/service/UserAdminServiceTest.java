package com.example.ticketing.domain.user.service;

import com.example.ticketing.domain.concert.entity.Concert;
import com.example.ticketing.domain.concert.enums.ConcertType;
import com.example.ticketing.domain.concert.repository.ConcertRepository;
import com.example.ticketing.domain.seat.entity.Seat;
import com.example.ticketing.domain.seat.entity.SeatDetail;
import com.example.ticketing.domain.seat.enums.SeatType;
import com.example.ticketing.domain.seat.repository.SeatDetailRepository;
import com.example.ticketing.domain.seat.repository.SeatRepository;
import com.example.ticketing.domain.ticket.repository.TicketRepository;
import com.example.ticketing.domain.user.dto.request.SeatDetailRequest;
import com.example.ticketing.domain.user.entity.User;
import com.example.ticketing.domain.user.enums.Gender;
import com.example.ticketing.domain.user.enums.UserRole;
import com.example.ticketing.domain.user.repository.UserRepository;
import com.example.ticketing.global.exception.CustomException;
import com.example.ticketing.global.exception.ExceptionType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAdminServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private EntityManager entityManager;
    @Mock
    private ConcertRepository concertRepository;
    @Mock
    private SeatRepository seatRepository;
    @Mock
    private SeatDetailRepository seatDetailRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TicketRepository ticketRepository;


    @InjectMocks
    private UserAdminService userAdminService;

    @Test
    void Concert_SeatDetail_Seat_생성에_성공한다() {
        //given
        LocalDateTime concertDate = LocalDateTime.of(2025, 5, 5, 20, 0); // 5월 1일
        LocalDateTime ticketingDate = LocalDateTime.of(2025, 5, 1, 10, 0); // 5월 5일

        //User user = new User("test@test.com", "qwer@1234", "test", UserRole.ADMIN, Gender.FEMALE);
        String concertName = "레베카";
        //ConcertType concertType = ConcertType.MUSICAL;
        int maxTickPerUser = 3;
        //boolean isDeleted = false;
        List<SeatDetailRequest> seatDetail = new ArrayList<>();
        SeatDetailRequest seatDetailRequest = new SeatDetailRequest("A_GRADE", 5000, 100);
        seatDetail.add(seatDetailRequest);


        // when
        userAdminService.createConcert(1L, concertName, concertDate, ticketingDate, "MUSICAL", maxTickPerUser, seatDetail);

        ArgumentCaptor<Concert> concertCaptor = ArgumentCaptor.forClass(Concert.class);
        verify(concertRepository, times(1)).save(concertCaptor.capture());

    }

    @Test
    void 생성하려는_콘서트의_concertDate가_ticketingDate_이전이면_예외가_발생한다() {
        //given
        LocalDateTime concertDate = LocalDateTime.of(2025, 5, 1, 20, 0); // 5월 1일
        LocalDateTime ticketingDate = LocalDateTime.of(2025, 5, 5, 10, 0); // 5월 5일

        //User user = new User("test@test.com", "qwer@1234", "test", UserRole.ADMIN, Gender.FEMALE);
        String concertName = "레베카";
        //ConcertType concertType = ConcertType.MUSICAL;
        int maxTickPerUser = 3;
        //boolean isDeleted = false;
        List<SeatDetailRequest> seatDetail = new ArrayList<>();
        SeatDetailRequest seatDetailRequest = new SeatDetailRequest("A_GRADE", 5000, 100);
        seatDetail.add(seatDetailRequest);

        //when
        CustomException exception = assertThrows(CustomException.class, () -> {
            userAdminService.createConcert(1L, concertName, concertDate, ticketingDate, "MUSICAL", maxTickPerUser, seatDetail);
        });

        //then
        assertEquals("콘서트 예매시간은 공연 시간보다 이전이어야 합니다.", exception.getMessage());
    }
}