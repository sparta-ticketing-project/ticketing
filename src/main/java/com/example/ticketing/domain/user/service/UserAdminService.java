package com.example.ticketing.domain.user.service;

import com.example.ticketing.domain.concert.entity.Concert;
import com.example.ticketing.domain.concert.enums.ConcertType;
import com.example.ticketing.domain.concert.repository.ConcertRepository;
import com.example.ticketing.domain.seat.entity.Seat;
import com.example.ticketing.domain.seat.entity.SeatDetail;
import com.example.ticketing.domain.seat.enums.SeatType;
import com.example.ticketing.domain.seat.repository.SeatDetailRepository;
import com.example.ticketing.domain.seat.repository.SeatRepository;
import com.example.ticketing.domain.seat.service.SeatService;
import com.example.ticketing.domain.ticket.entity.Ticket;
import com.example.ticketing.domain.ticket.enums.TicketStatus;
import com.example.ticketing.domain.ticket.repository.TicketRepository;
import com.example.ticketing.domain.user.dto.request.SeatDetailRequest;
import com.example.ticketing.domain.user.dto.request.UpdateConcertSeatDetailRequest;
import com.example.ticketing.domain.user.dto.request.UpdateSeatRequest;
import com.example.ticketing.domain.user.dto.response.ConcertResponse;
import com.example.ticketing.domain.user.dto.response.SeatDetailResponse;
import com.example.ticketing.domain.user.dto.response.SeatResponse;
import com.example.ticketing.domain.user.dto.response.UpdatedSeatResponse;
import com.example.ticketing.domain.user.entity.User;
import com.example.ticketing.domain.user.repository.UserRepository;
import com.example.ticketing.global.exception.CustomException;
import com.example.ticketing.global.exception.ExceptionType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAdminService {

    private final SeatService seatService;
    private final SeatBatchInsertService seatBatchInsertService;
    private final SeatBatchUpdateService seatBatchUpdateService;

    private final ConcertRepository concertRepository;
    private final SeatRepository seatRepository;
    private final SeatDetailRepository seatDetailRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    private final JdbcTemplate jdbcTemplate;
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public ConcertResponse createConcert(Long userId, String concertName, LocalDateTime concertDate, LocalDateTime ticketingDate, String concertType, int maxTicketPerUser, List<SeatDetailRequest> seatDetail) {

        IsTicketingDateBeforeConcertDate(ticketingDate, concertDate);

        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ExceptionType.USER_NOT_FOUND)
        );


        // Concert 생성
        Concert concert = Concert.builder()
                .user(user)
                .concertName(concertName)
                .concertDate(concertDate)
                .ticketingDate(ticketingDate)
                .concertType(ConcertType.fromString(concertType))
                .maxTicketPerUser(maxTicketPerUser)
                .isDeleted(false)
                .build();

        concertRepository.save(concert);

        // Bulk Insert할 Seat를 담는 리스트
        List<Seat> newSeats = new ArrayList<>();

        // SeatDetailResponse를 만들기 위해 SeatDetail을 담는 리스트
        List<SeatDetail> seatDetailToResponse = new ArrayList<>();

        int concertTotalSeatCount = 0;

        // SeatDetail 생성 + Seat 생성(newSeats에 Seat 담기)
        for (SeatDetailRequest dto : seatDetail) {

            // SeatDetail 생성
            String seatType = dto.getSeatType();
            int price = dto.getPrice();
            int totalSeatCountPerSeatType = dto.getTotalSeatCount();
            concertTotalSeatCount += totalSeatCountPerSeatType;

            SeatDetail newSeatDetail = SeatDetail.builder()
                    .concert(concert)
                    .seatType(SeatType.fromString(seatType))
                    .price(price)
                    .totalSeatCount(totalSeatCountPerSeatType)
                    .availableSeatCount(totalSeatCountPerSeatType) // 초기 available 좌석 수 = 총 좌석 수
                    .build();

            seatDetailToResponse.add(newSeatDetail);
            seatDetailRepository.save(newSeatDetail);

            // Seat 생성 (seatType 별로 seatNumber 1부터 증가)
            for (int seatNumber = 1; seatNumber <= totalSeatCountPerSeatType; seatNumber++) {
                Seat seat = Seat.builder()
                        .concert(concert)
                        .seatDetail(newSeatDetail)
                        .isAvailable(true)
                        .seatNumber(seatNumber)
                        .build();
                newSeats.add(seat);
            }
        }

        // Seat Bulk Insert
        seatBatchInsert(newSeats);

        // Concert의 totalSeatCount와 availableSeatCount 업데이트
        concert.setTotalSeatCount(concertTotalSeatCount);
        concert.setAvailableSeatCount(concertTotalSeatCount);

        return new ConcertResponse(
                concert.getId(),
                concert.getUser().getId(),
                concert.getConcertName(),
                concert.getConcertDate(),
                concert.getTicketingDate(),
                concert.getConcertType().getDescription(),
                concert.getMaxTicketPerUser(),
                concert.getTotalSeatCount(),
                concert.getAvailableSeatCount(),
                concert.getCreatedAt(),
                concert.getModifiedAt(),
                concert.getIsDeleted(),
                createSeatDetailResponse(seatDetailToResponse)
        );
    }

    @Transactional
    public ConcertResponse updateConcert(Long userId, Long concertId, String concertName, LocalDateTime concertDate, LocalDateTime ticketingDate, String concertType, int maxTicketPerUser, List<UpdateConcertSeatDetailRequest> seatDetail) {

        Concert concert = concertRepository.findById(concertId).orElseThrow(
                () -> new CustomException(ExceptionType.CONCERT_NOT_FOUND)
        );

        if (concert.getUser().getId() != userId) {
            throw new CustomException(ExceptionType.NO_PERMISSION_ACTION);
        }

        IsNowBeforeTicketingDate(LocalDateTime.now(), concert.getTicketingDate());

        List<SeatDetail> existingSeatDetails = seatDetailRepository.findByConcertId(concertId)
                .orElseThrow(() -> new CustomException(ExceptionType.SEAT_DETAIL_NOT_FOUND));

        // Concert 업데이트
        concert.updateConcert(concertName, concertDate, ticketingDate, ConcertType.fromString(concertType), maxTicketPerUser);

        // SeatDetailResponse를 만들기 위해 SeatDetail을 담는 리스트
        List<SeatDetail> seatDetailToResponse = new ArrayList<>();

        // price 수정
        if (seatDetail != null) {
            Map<SeatType, UpdateConcertSeatDetailRequest> updateSeatDetailMap = seatDetail.stream().collect(Collectors.toMap(s -> SeatType.fromString(s.getSeatType()), s -> s));

            for (SeatDetail existingSeatDetail : existingSeatDetails) {
                SeatType seatType = existingSeatDetail.getSeatType();

               if(updateSeatDetailMap.get(seatType) != null) {
                   existingSeatDetail.updatePrice(updateSeatDetailMap.get(seatType).getPrice());
               }
                seatDetailToResponse.add(existingSeatDetail);

            }
        } else {
            seatDetailToResponse = existingSeatDetails;
        }

        return new ConcertResponse(
                concert.getId(),
                1L,
                concert.getConcertName(),
                concert.getConcertDate(),
                concert.getTicketingDate(),
                concert.getConcertType().getDescription(),
                concert.getMaxTicketPerUser(),
                concert.getTotalSeatCount(),
                concert.getAvailableSeatCount(),
                concert.getCreatedAt(),
                concert.getModifiedAt(),
                concert.getIsDeleted(),
                createSeatDetailResponse(seatDetailToResponse)
        );

    }

    @Transactional
    public UpdatedSeatResponse updateSeats(Long concertId, Long seatDetailId, UpdateSeatRequest dto) {
        //바뀐 Seat의 SeatDetail의 availableSeatCount 계산해서 업데이트
        //바뀐 SeatDetail의 Concert의 availableSeatCount 계산해서 업데이트

        String currentTransactionName = TransactionSynchronizationManager.getCurrentTransactionName();
        System.out.println(currentTransactionName);

        // Seat에서 SeatDetailId가 seatDetailId 인것들 중 seatNumber가 dto에 있는 seatNumber인 것들을 조회
        List<Integer> toChangeAvailableSeatNumberList = dto.getAvailableSeatList();
        List<Integer> toChangeUnavailableSeatNumberList = dto.getUnavailableSeatList();

        List<Seat> toChangeAvailableSeats = seatRepository.findBySeatDetailIdAndSeatNumberIn(seatDetailId, toChangeAvailableSeatNumberList);
        List<Seat> toChangeUnavailableSeats = seatRepository.findBySeatDetailIdAndSeatNumberIn(seatDetailId, toChangeUnavailableSeatNumberList);


        seatBatchUpdateService.seatBatchUpdate(toChangeAvailableSeats, true);
        seatService.updateAvailableSeatCount(toChangeAvailableSeats.size(), seatDetailId, concertId, "+");

        seatBatchUpdateService.seatBatchUpdate(toChangeUnavailableSeats, false);
        seatService.updateAvailableSeatCount(toChangeUnavailableSeats.size(), seatDetailId, concertId, "-");

        entityManager.flush();
        entityManager.clear();
        List<Seat> changedToAvailalbeSeatList = seatRepository.findBySeatDetailIdAndSeatNumberIn(seatDetailId, toChangeAvailableSeatNumberList);
        List<Seat> changedToUnavailableSeatList = seatRepository.findBySeatDetailIdAndSeatNumberIn(seatDetailId, toChangeUnavailableSeatNumberList);


        List<SeatResponse> availableSeatDtos = changedToAvailalbeSeatList.stream()
                .map(seat -> new SeatResponse(seat.getId(), seat.getSeatNumber(), seat.getIsAvailable()))
                .toList();

        List<SeatResponse> unavailableSeatDtos = changedToUnavailableSeatList.stream()
                .map(seat -> new SeatResponse(seat.getId(), seat.getSeatNumber(), seat.getIsAvailable()))
                .toList();

        return new UpdatedSeatResponse(availableSeatDtos, unavailableSeatDtos);

    }

    @Transactional(readOnly = true)
    public Page<ConcertResponse> getConcerts(Long userId, int page, int size) {
        Sort sortStandard = Sort.by("concertDate").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sortStandard);

        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ExceptionType.USER_NOT_FOUND)
        );

        Page<Concert> concerts = concertRepository.findConcertsByUser(user, pageable).orElseThrow(
                () -> new CustomException(ExceptionType.CONCERT_NOT_FOUND)
        );

        Page<ConcertResponse> result = concerts.map(concert -> {
            List<SeatDetailResponse> seatDetailResponses = new ArrayList<>();
            List<SeatDetail> seatDetails = seatDetailRepository.findByConcertId(concert.getId()).orElseThrow(
                    () -> new CustomException(ExceptionType.SEAT_DETAIL_NOT_FOUND)
            );

            for (SeatDetail seatDetail : seatDetails) {
                SeatDetailResponse response = new SeatDetailResponse(
                        seatDetail.getId(),
                        seatDetail.getConcert().getId(),
                        seatDetail.getSeatType(),
                        seatDetail.getPrice(),
                        seatDetail.getTotalSeatCount(),
                        seatDetail.getAvailableSeatCount()
                );
                seatDetailResponses.add(response);
            }

            return new ConcertResponse(
                    concert.getId(),
                    concert.getUser().getId(),
                    concert.getConcertName(),
                    concert.getConcertDate(),
                    concert.getTicketingDate(),
                    concert.getConcertType().getDescription(),
                    concert.getMaxTicketPerUser(),
                    concert.getTotalSeatCount(),
                    concert.getAvailableSeatCount(),
                    concert.getCreatedAt(),
                    concert.getModifiedAt(),
                    concert.getIsDeleted(),
                    seatDetailResponses);
        });

        return result;
    }

    @Transactional
    public void seatBatchInsert(List<Seat> seats) {
        String sql = "INSERT INTO seats (concert_id, seat_details_id, seat_number, is_available, created_at, modified_at) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Seat seat = seats.get(i);
                ps.setLong(1, seat.getConcert().getId());  // 1번째 파라미터: concert_id
                ps.setLong(2, seat.getSeatDetail().getId());  // 2번째 파라미터: seat_detail_id
                ps.setInt(3, seat.getSeatNumber());  // 3번째 파라미터: seat_number
                ps.setBoolean(4, seat.getIsAvailable());  // 4번째 파라미터: is_available
                ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            }

            @Override
            public int getBatchSize() {
                return seats.size();
            }
        }
        );
    }

    // ticketingDate가 concertDate보다 이전이어야 한다.
    void IsTicketingDateBeforeConcertDate (LocalDateTime ticketingDate, LocalDateTime concertDate) {
        if (!ticketingDate.isBefore(concertDate)) {
            throw new CustomException(ExceptionType.INVALID_TICKETING_DATE);
        }
    }

    // 수정하려는 시간이 ticketingDate 이전이어야 한다.
    void IsNowBeforeTicketingDate (LocalDateTime now, LocalDateTime ticketingDate) {
        if (!now.isBefore(ticketingDate)) {
            throw new CustomException(ExceptionType.CONCERT_MODIFICATION_NOT_ALLOWED);
        }
    }


    List<SeatDetailResponse> createSeatDetailResponse(List<SeatDetail> seatDetails) {
        return seatDetails.stream().map(
                s -> new SeatDetailResponse(
                     s.getId(),
                     s.getConcert().getId(),
                     s.getSeatType(),
                     s.getPrice(),
                     s.getTotalSeatCount(),
                     s.getAvailableSeatCount()
                )).toList();
    }


    @Transactional
    public void deleteConcert(Long userId, Long concertId) {
        Concert concert = concertRepository.findById(concertId).orElseThrow(
                () -> new CustomException(ExceptionType.CONCERT_NOT_FOUND)
        );

        if (concert.getUser().getId() != userId) {
            throw new CustomException(ExceptionType.NO_PERMISSION_ACTION);
        }

        // 삭제하려는 시점이 ticketingDate 이후 concertDate 이전이라면
        // 해당 콘서트의 티켓에 ticketStatus가 available한 티켓이 있다면 삭제 불가

        if (LocalDateTime.now().isAfter(concert.getTicketingDate()) && LocalDateTime.now().isBefore(concert.getConcertDate())) {
            Optional<Ticket> concertWithTicketStatus = ticketRepository.findByConcertWithTicketStatus(concert, TicketStatus.AVAILABLE);
            concertWithTicketStatus.ifPresent(
                    c -> {throw new CustomException(ExceptionType.CONCERT_DELETION_NOT_ALLOWED);}
            );
        }

        concert.setIsDeleted(true);

    }

    // 권한검증 필요
    public ConcertResponse getConcert(Long concertId) {
        Concert concert = concertRepository.findById(concertId).orElseThrow(
                () -> new CustomException(ExceptionType.CONCERT_NOT_FOUND)
        );

        List<SeatDetail> seatDetails = seatDetailRepository.findByConcertId(concertId).orElseThrow(
                () -> new CustomException(ExceptionType.SEAT_DETAIL_NOT_FOUND)
        );

        return new ConcertResponse(
                concert.getId(),
                concert.getUser().getId(),
                concert.getConcertName(),
                concert.getConcertDate(),
                concert.getTicketingDate(),
                concert.getConcertType().getDescription(),
                concert.getMaxTicketPerUser(),
                concert.getTotalSeatCount(),
                concert.getAvailableSeatCount(),
                concert.getCreatedAt(),
                concert.getModifiedAt(),
                concert.getIsDeleted(),
                createSeatDetailResponse(seatDetails)
        );
    }

}
