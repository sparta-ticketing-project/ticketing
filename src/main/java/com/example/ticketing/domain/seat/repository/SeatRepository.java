package com.example.ticketing.domain.seat.repository;

import com.example.ticketing.domain.seat.entity.Seat;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    @EntityGraph(attributePaths = {"seatDetail", "concert"})
    @Query("SELECT s From Seat s WHERE s.id IN :seatIds")
    List<Seat> findAllWithSeatDetailAndConcertByIdIn(List<Long> seatIds);
}
