package com.example.ticketing.domain.seat.repository;

import com.example.ticketing.domain.concert.entity.Concert;
import com.example.ticketing.domain.seat.entity.SeatDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeatDetailRepository extends JpaRepository<SeatDetail, Long> {
    @Query("SELECT s FROM SeatDetail s WHERE s.concert.id = :concertId")
    List<SeatDetail> findSeatDetailsByConcertId(@Param(value = "concertId") Long concertId);

    Optional<List<SeatDetail>> findByConcert(Concert concert);
}
