package com.example.ticketing.domain.seat.repository;

import com.example.ticketing.domain.seat.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    Optional<List<Seat>> findBySeatDetailId(Long seatDetailId);

    @Query("SELECT s FROM Seat s WHERE s.seatDetail.id = :seatDetailId AND s.seatNumber IN :seatNumbers")
    List<Seat> findBySeatDetailIdAndSeatNumberIn(@Param("seatDetailId") Long seatDetailId, @Param("seatNumbers") List<Integer> seatNumbers);

    @Query("SELECT COUNT(s) FROM Seat s WHERE s.seatDetail.id = :seatDetailId AND s.isAvailable = false")
    int countUnavailableSeatsBySeatDetailId(@Param("seatDetailId") Long seatDetailId);
}
