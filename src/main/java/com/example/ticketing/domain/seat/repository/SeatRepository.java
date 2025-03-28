package com.example.ticketing.domain.seat.repository;

import com.example.ticketing.domain.seat.entity.Seat;
import org.springframework.data.jpa.repository.EntityGraph;
import com.example.ticketing.domain.seat.entity.SeatDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    @Query("SELECT s FROM Seat s WHERE s.seatDetail.id = :seatDetailId AND s.seatNumber IN :seatNumbers")
    List<Seat> findBySeatDetailIdAndSeatNumberIn(@Param("seatDetailId") Long seatDetailId, @Param("seatNumbers") List<Integer> seatNumbers);

    @EntityGraph(attributePaths = {"seatDetail", "concert"})
    @Query("SELECT s From Seat s WHERE s.id IN :seatIds")
    List<Seat> findAllWithSeatDetailAndConcertByIdIn(List<Long> seatIds);
}
