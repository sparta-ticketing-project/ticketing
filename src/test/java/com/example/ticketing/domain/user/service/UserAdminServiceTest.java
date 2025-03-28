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
import com.example.ticketing.domain.user.dto.request.UpdateConcertRequest;
import com.example.ticketing.domain.user.dto.request.UpdateConcertSeatDetailRequest;
import com.example.ticketing.domain.user.dto.response.ConcertResponse;
import com.example.ticketing.domain.user.dto.response.SeatDetailResponse;
import com.example.ticketing.domain.user.entity.User;
import com.example.ticketing.domain.user.enums.Gender;
import com.example.ticketing.domain.user.enums.UserRole;
import com.example.ticketing.domain.user.repository.UserRepository;
import com.example.ticketing.global.dto.AuthUser;
import com.example.ticketing.global.exception.CustomException;
import com.example.ticketing.global.exception.ExceptionType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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

    private User createMockUser() {
        return User.builder()
                .email("test@test.com")
                .password("qwer@1234")
                .username("test")
                .userRole(UserRole.ADMIN)
                .gender(Gender.FEMALE)
                .age(25)
                .build();
    }

    private Concert createMockConcert(User mockUser) {
        return Concert.builder()
                .user(mockUser)
                .concertName("레베카")
                .concertType(ConcertType.MUSICAL)
                .concertDate(LocalDateTime.of(2025, 5, 5, 20, 0))
                .ticketingDate(LocalDateTime.of(2025, 5, 1, 10, 0))
                .maxTicketPerUser(3)
                .isDeleted(false)
                .build();
    }

    private List<SeatDetail> createMockSeatDetails(Concert concert) {
        return Arrays.asList(
                SeatDetail.builder()
                        .concert(concert)
                        .seatType(SeatType.S_GRADE)
                        .price(5000)
                        .build(),
                SeatDetail.builder()
                        .concert(concert)
                        .seatType(SeatType.A_GRADE)
                        .price(4000)
                        .build()
        );
    }

    @Test
    void Concert_SeatDetail_Seat_생성에_성공한다() {

        User mockUser = createMockUser();

        String concertName = "데스노트";
        String concertType = "MUSICAL";
        int maxTicketPerUser = 3;
        LocalDateTime concertDate = LocalDateTime.of(2025, 5, 5, 20, 0); // 5월 1일
        LocalDateTime ticketingDate = LocalDateTime.of(2025, 5, 1, 10, 0); // 5월 5일
        List<SeatDetailRequest> seatDetail = new ArrayList<>();
        SeatDetailRequest seatDetailRequest = new SeatDetailRequest("A_GRADE", 5000, 100);
        seatDetail.add(seatDetailRequest);

        AuthUser authUser = AuthUser.builder().userId(1L).userRole(UserRole.ADMIN).build();

        // when
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        userAdminService.createConcert(authUser, concertName, concertDate, ticketingDate, concertType, maxTicketPerUser, seatDetail);
        userAdminService.createConcert(authUser, concertName, concertDate, ticketingDate, "MUSICAL", maxTicketPerUser, seatDetail);

        ArgumentCaptor<Concert> concertCaptor = ArgumentCaptor.forClass(Concert.class);

        // then
        verify(concertRepository, times(1)).save(concertCaptor.capture());

        Concert savedConcert = concertCaptor.getValue();
        assertNotNull(savedConcert);
        assertEquals(concertName, savedConcert.getConcertName());
        assertEquals(concertDate, savedConcert.getConcertDate());
        assertEquals(ticketingDate, savedConcert.getTicketingDate());
        assertEquals(maxTicketPerUser, savedConcert.getMaxTicketPerUser());

        verify(seatDetailRepository, times(2)).save(any(SeatDetail.class));

        verify(jdbcTemplate).batchUpdate(
                anyString(),
                any(BatchPreparedStatementSetter.class)
        );
    }

    @Test
    void 생성하려는_콘서트의_concertDate가_ticketingDate_이전이면_예외가_발생한다() {

        //given
        User mockUser = createMockUser();

        LocalDateTime concertDate = LocalDateTime.of(2025, 5, 1, 20, 0); // 5월 1일
        LocalDateTime ticketingDate = LocalDateTime.of(2025, 5, 5, 10, 0); // 5월 5일

        AuthUser authUser = AuthUser.builder().userId(1L).userRole(UserRole.ADMIN).build();
        String concertName = "레베카";
        String concertType = "MUSICAL";
        int maxTicketPerUser = 3;
        List<SeatDetailRequest> seatDetail = new ArrayList<>();
        SeatDetailRequest seatDetailRequest = new SeatDetailRequest("A_GRADE", 5000, 100);
        seatDetail.add(seatDetailRequest);

        //when
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        CustomException exception = assertThrows(CustomException.class, () -> {
            userAdminService.createConcert(authUser, concertName, concertDate, ticketingDate, concertType, maxTicketPerUser, seatDetail);
        });

        //then
        assertEquals("콘서트 예매시간은 공연 시간보다 이전이어야 합니다.", exception.getMessage());
    }

    @Test
    void 콘서트_수정에_성공한다() {

    }

    @Test
    void 좌석_수정에_성공한다() {

    }

    @Test
    void 콘서트_삭제에_성공한다() {

    }

    @Test
    void 성별_예매_통계를_성공한다() {

    }
}