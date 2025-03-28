package com.example.ticketing.domain.user.service;

import com.example.ticketing.aop.AdminOnly;
import com.example.ticketing.domain.concert.entity.Concert;
import com.example.ticketing.domain.concert.enums.ConcertType;
import com.example.ticketing.domain.concert.repository.ConcertRepository;
import com.example.ticketing.domain.seat.entity.Seat;
import com.example.ticketing.domain.seat.entity.SeatDetail;
import com.example.ticketing.domain.seat.enums.SeatType;
import com.example.ticketing.domain.seat.repository.SeatDetailRepository;
import com.example.ticketing.domain.seat.repository.SeatRepository;
import com.example.ticketing.domain.ticket.entity.Ticket;
import com.example.ticketing.domain.ticket.enums.TicketStatus;
import com.example.ticketing.domain.ticket.repository.TicketRepository;
import com.example.ticketing.domain.user.dto.request.SeatDetailRequest;
import com.example.ticketing.domain.user.dto.request.UpdateConcertSeatDetailRequest;
import com.example.ticketing.domain.user.dto.request.UpdateSeatRequest;
import com.example.ticketing.domain.user.dto.response.*;
import com.example.ticketing.domain.user.entity.User;
import com.example.ticketing.domain.user.enums.Gender;
import com.example.ticketing.domain.user.enums.UserRole;
import com.example.ticketing.domain.user.repository.UserRepository;
import com.example.ticketing.global.auth.Auth;
import com.example.ticketing.global.dto.AuthUser;
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

    private final ConcertRepository concertRepository;
    private final SeatRepository seatRepository;
    private final SeatDetailRepository seatDetailRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    private final JdbcTemplate jdbcTemplate;
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public ConcertResponse createConcert(AuthUser authUser, String concertName, LocalDateTime concertDate, LocalDateTime ticketingDate, String concertType, int maxTicketPerUser, List<SeatDetailRequest> seatDetail) {

        IsTicketingDateBeforeConcertDate(ticketingDate, concertDate);

        User user = userRepository.findById(authUser.getUserId()).orElseThrow(
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

        // SeatDetailResponse를 만들기 위해 SeatDetail을 담는 리스트
        List<SeatDetail> seatDetailToResponse = new ArrayList<>();

        int concertTotalSeatCount = 0;

        // Bulk Insert할 Seat를 담는 리스트
        List<Seat> newSeats = new ArrayList<>();

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
    public ConcertResponse updateConcert(AuthUser authUser, Long concertId, String concertName, LocalDateTime concertDate, LocalDateTime ticketingDate, String concertType, int maxTicketPerUser, List<UpdateConcertSeatDetailRequest> updateSeatDetailRequest) {

        Concert concert = concertRepository.findById(concertId).orElseThrow(
                () -> new CustomException(ExceptionType.CONCERT_NOT_FOUND)
        );

        // 본인이 생성하지 않은 콘서트일 경우 예외발생
        if (concert.getUser().getId() != authUser.getUserId()) {
            throw new CustomException(ExceptionType.NO_PERMISSION_ACTION);
        }

        // 수정하려는 시점이 ticketingDate 이전이어야 한다.
        IsNowBeforeTicketingDate(LocalDateTime.now(), concert.getTicketingDate());

        // Concert 업데이트
        concert.updateConcert(concertName, concertDate, ticketingDate, ConcertType.fromString(concertType), maxTicketPerUser);

        List<SeatDetail> existingSeatDetails = seatDetailRepository.findByConcert(concert)
                .orElseThrow(() -> new CustomException(ExceptionType.SEAT_DETAIL_NOT_FOUND));

        // price 업데이트
        if (updateSeatDetailRequest != null) {

            for (UpdateConcertSeatDetailRequest request : updateSeatDetailRequest) {

                SeatType seatType = SeatType.fromString(request.getSeatType());

                for (SeatDetail existingSeatDetail : existingSeatDetails) {
                    if(existingSeatDetail.getSeatType() == seatType) {
                        existingSeatDetail.updatePrice(request.getPrice());
                    }
                }
            }
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
                createSeatDetailResponse(existingSeatDetails)
        );

    }

    @Transactional
    public UpdatedSeatResponse updateSeats(AuthUser authUser, Long concertId, Long seatDetailId, UpdateSeatRequest dto) {

        Concert concert = concertRepository.findById(concertId).orElseThrow(
                () -> new CustomException(ExceptionType.CONCERT_NOT_FOUND)
        );

        // 자신이 생성한 콘서트의 좌석만 수정할 수 있도록
        if (concert.getUser().getId() != authUser.getUserId()) {
            throw new CustomException(ExceptionType.SEAT_UPDATE_NOT_ALLOWED);
        }

        IsNowBeforeTicketingDate(LocalDateTime.now(), concert.getTicketingDate());

        // 수정할 SeatNumber들이 담겨있는 리스트
        List<Integer> toChangeAvailableSeatNumberList = dto.getAvailableSeatList();
        List<Integer> toChangeUnavailableSeatNumberList = dto.getUnavailableSeatList();

        // 수정할 Seat가 담겨있는 리스트
        List<Seat> toChangeAvailableSeatList = seatRepository.findBySeatDetailIdAndSeatNumberIn(seatDetailId, toChangeAvailableSeatNumberList);
        List<Seat> toChangeUnavailableSeatList = seatRepository.findBySeatDetailIdAndSeatNumberIn(seatDetailId, toChangeUnavailableSeatNumberList);

        // Seat의 isAvailable 변경 + SeatDetail, Concert의 availableSeatCount 업데이트
        seatBatchUpdate(toChangeAvailableSeatList, true);
        updateAvailableSeatCount(toChangeAvailableSeatList.size(), seatDetailId, concertId, "+");

        seatBatchUpdate(toChangeUnavailableSeatList, false);
        updateAvailableSeatCount(toChangeUnavailableSeatList.size(), seatDetailId, concertId, "-");

        entityManager.flush();
        // Seat가 업데이트 되기 전 캐시에 있던 List<Seat> 삭제
        entityManager.clear();

        List<Seat> modifiedAvailableSeatList = seatRepository.findBySeatDetailIdAndSeatNumberIn(seatDetailId, toChangeAvailableSeatNumberList);
        List<Seat> modifiedUnavailableSeatList = seatRepository.findBySeatDetailIdAndSeatNumberIn(seatDetailId, toChangeUnavailableSeatNumberList);

        List<SeatResponse> availableSeatDtos = modifiedAvailableSeatList.stream()
                .map(seat -> new SeatResponse(seat.getId(), seat.getSeatNumber(), seat.getIsAvailable()))
                .toList();

        List<SeatResponse> unavailableSeatDtos = modifiedUnavailableSeatList.stream()
                .map(seat -> new SeatResponse(seat.getId(), seat.getSeatNumber(), seat.getIsAvailable()))
                .toList();


        return new UpdatedSeatResponse(availableSeatDtos, unavailableSeatDtos);

    }

    @Transactional(readOnly = true)
    public Page<ConcertResponse> getConcerts(AuthUser authUser, int page, int size) {

        User user = userRepository.findById(authUser.getUserId()).orElseThrow(
                () -> new CustomException(ExceptionType.USER_NOT_FOUND)
        );

        Sort sortStandard = Sort.by("concertDate").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sortStandard);

        Page<Concert> concerts = concertRepository.findConcertsByUser(user, pageable).orElseThrow(
                () -> new CustomException(ExceptionType.CONCERT_NOT_FOUND)
        );

        Page<ConcertResponse> result = concerts.map(concert -> {

            List<SeatDetail> seatDetails = seatDetailRepository.findByConcert(concert).orElseThrow(
                    () -> new CustomException(ExceptionType.SEAT_DETAIL_NOT_FOUND)
            );

            List<SeatDetailResponse> seatDetailResponse = createSeatDetailResponse(seatDetails);

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
                    seatDetailResponse);
        });

        return result;
    }

    @Transactional(readOnly = true)
    public ConcertResponse getConcert(AuthUser authUser, Long concertId) {
        Concert concert = concertRepository.findById(concertId).orElseThrow(
                () -> new CustomException(ExceptionType.CONCERT_NOT_FOUND)
        );

        List<SeatDetail> seatDetails = seatDetailRepository.findByConcert(concert).orElseThrow(
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

    @Transactional
    public void deleteConcert(AuthUser authUser, Long concertId) {
        Concert concert = concertRepository.findById(concertId).orElseThrow(
                () -> new CustomException(ExceptionType.CONCERT_NOT_FOUND)
        );

        // 본인이 생성한 콘서트만 삭제 가능해야한다.
        if (concert.getUser().getId() != authUser.getUserId()) {
            throw new CustomException(ExceptionType.NO_PERMISSION_ACTION);
        }

        // 삭제 시점이 ticketingDate 이후 concertDate 이전일 경우
        // AVAILABLE한 ticket이 있다면 삭제 불가(환불 처리 로직은 만들지 않았음)
        if (LocalDateTime.now().isAfter(concert.getTicketingDate()) && LocalDateTime.now().isBefore(concert.getConcertDate())) {
            Optional<List<Ticket>> concertWithTicketStatus = ticketRepository.findByConcertWithTicketStatus(concert, TicketStatus.AVAILABLE);
            concertWithTicketStatus.ifPresent(
                    c -> {throw new CustomException(ExceptionType.CONCERT_DELETION_NOT_ALLOWED);}
            );
        }

        concert.setIsDeleted(true);

    }

    @Transactional(readOnly = true)
    public GenderBookingRateResponse bookingRateByGender(Long concertId) {

        Concert concert = concertRepository.findById(concertId).orElseThrow(
                () -> new CustomException(ExceptionType.CONCERT_NOT_FOUND)
        );

        int ticketCountByConcert = ticketRepository.countTicketByConcert(concert);

        // female
        int femaleCount = ticketRepository.countTicketByConcertWithTicketStatusAndGender(concert, TicketStatus.AVAILABLE, Gender.FEMALE);
        double femaleResult = ((double) femaleCount / ticketCountByConcert) * 100;

        // male
        int maleCount = ticketRepository.countTicketByConcertWithTicketStatusAndGender(concert, TicketStatus.AVAILABLE, Gender.MALE);
        double maleResult = ((double) maleCount / ticketCountByConcert) * 100;

        return new GenderBookingRateResponse(femaleResult, maleResult);
    }

    // ticketingDate가 concertDate보다 이전이어야 한다.
    void IsTicketingDateBeforeConcertDate (LocalDateTime ticketingDate, LocalDateTime concertDate) {
        if (!ticketingDate.isBefore(concertDate)) {
            throw new CustomException(ExceptionType.INVALID_TICKETING_DATE);
        }
    }

    // 수정하려는 시점이 ticketingDate 이전이어야 한다.
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

    public void seatBatchInsert(List<Seat> seats) {
        System.out.println("seatBatchInsert 실행됨!");

        int batchSize = 1000;
        String sql = "INSERT INTO seats (concert_id, seat_details_id, seat_number, is_available, created_at, modified_at) VALUES (?, ?, ?, ?, ?, ?)";

        for (int i = 0; i < seats.size(); i += batchSize ) {
            List<Seat> batch = seats.subList(i, Math.min(i + batchSize, seats.size()));

            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int j) throws SQLException {
                            Seat seat = batch.get(j);
                            ps.setLong(1, seat.getConcert().getId());  // 1번째 파라미터: concert_id
                            ps.setLong(2, seat.getSeatDetail().getId());  // 2번째 파라미터: seat_detail_id
                            ps.setInt(3, seat.getSeatNumber());  // 3번째 파라미터: seat_number
                            ps.setBoolean(4, seat.getIsAvailable());  // 4번째 파라미터: is_available
                            ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                            ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
                        }

                        @Override
                        public int getBatchSize() {
                            return batch.size();
                        }
                    }
            );

        }
    }

    public void seatBatchUpdate(List<Seat> seats, boolean tochange) {

        int batchSize = 1000;
        String sql = "UPDATE seats SET is_available = ?, modified_at = ? WHERE id = ?";

        for (int i = 0; i < seats.size(); i += batchSize) {
            List<Seat> batch = seats.subList(i, Math.min(i + batchSize, seats.size()));

            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int j) throws SQLException {
                    Seat seat = batch.get(j); // Seat 객체를 사용하여 id와 상태를 업데이트
                    ps.setBoolean(1, tochange);  // 1번째 파라미터: is_available
                    ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));  // 2번째 파라미터: modified_at
                    ps.setLong(3, seat.getId());  // 3번째 파라미터: id
                }

                @Override
                public int getBatchSize() {
                    return batch.size();
                }
            });
        }

        // 영속성 컨텍스트에서 Seat 객체를 찾아 DB와 동기화 해주려 했지만
        // 조회하는 쿼리가 다량 발생
        // 차라리 .clear() 후 한번만 조회해오는 것이 더 효율적
//        for (Seat seat : seats) {
//            Seat modifiedSeat = entityManager.find(Seat.class, seat.getId());  // 영속성 컨텍스트에서 엔티티 찾기
//            if (modifiedSeat != null) {
//                modifiedSeat.setAvailable(tochange);  // 상태 변경
//            }
//        }
    }

    public void updateAvailableSeatCount(int changedSeatCount, Long seatDetailId, Long concertId, String operation) {

        SeatDetail seatDetail = seatDetailRepository.findById(seatDetailId).orElseThrow(
                () -> new CustomException(ExceptionType.SEAT_DETAIL_NOT_FOUND)
        );
        int seatDetailAvailableSeatCount = seatDetail.getAvailableSeatCount();

        Concert concert = concertRepository.findById(concertId).orElseThrow(
                () -> new CustomException(ExceptionType.CONCERT_NOT_FOUND)
        );
        int totalAvailableSeatCount = concert.getAvailableSeatCount();

        switch (operation) {
            case "+":
                seatDetailAvailableSeatCount += changedSeatCount;
                totalAvailableSeatCount += changedSeatCount;
                seatDetail.setAvailableSeatCount(seatDetailAvailableSeatCount);
                concert.setAvailableSeatCount(totalAvailableSeatCount);
                break;

            case "-":
                seatDetailAvailableSeatCount -= changedSeatCount;
                totalAvailableSeatCount -= changedSeatCount;
                seatDetail.setAvailableSeatCount(seatDetailAvailableSeatCount);
                concert.setAvailableSeatCount(totalAvailableSeatCount);
                break;
        }
    }
}
