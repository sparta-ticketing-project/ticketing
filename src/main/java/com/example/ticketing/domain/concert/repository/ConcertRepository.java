package com.example.ticketing.domain.concert.repository;


import com.example.ticketing.domain.concert.entity.Concert;
import com.example.ticketing.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import javax.swing.text.html.Option;
import java.util.Optional;

public interface ConcertRepository extends JpaRepository<Concert, Long>, ConcertRepositoryCustom {
    @Query("SELECT c FROM Concert c WHERE c.isDeleted = false ORDER BY c.viewCount DESC")
    Page<Concert> findPopularConcerts(Pageable pageable);

    @Query("SELECT c FROM Concert c WHERE c.id = :concertId AND c.isDeleted = false")
    Optional<Concert> findById(@Param(value = "concertId") Long concertId);

    @Query("select c from Concert c where c.user = :user and c.isDeleted = false")
    Optional<Page<Concert>> findConcertsByUser(@Param("user") User user, Pageable pageable);

}
