package com.example.ticketing.domain.seat.repository;

import com.example.ticketing.domain.concert.entity.Concert;
import com.example.ticketing.domain.seat.entity.SeatDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SeatDetailRepository extends JpaRepository<SeatDetail, Long> {

    Optional<List<SeatDetail>> findByConcert(Concert concert);
}
