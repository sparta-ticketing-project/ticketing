package com.example.ticketing.domain.ticket.repository;

import com.example.ticketing.domain.concert.entity.Concert;
import com.example.ticketing.domain.order.entity.Order;
import com.example.ticketing.domain.ticket.entity.Ticket;
import com.example.ticketing.domain.ticket.enums.TicketStatus;
import com.example.ticketing.domain.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import com.example.ticketing.domain.user.enums.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @EntityGraph(attributePaths = {"seat", "seat.seatDetail"})
    List<Ticket> findAllWithSeatAndSeatDetailByOrder(Order order);
    int countByConcertAndUserAndTicketStatus(Concert concert, User user, TicketStatus ticketStatus);
    @Query("select t from Ticket t where t.concert = :concert and t.ticketStatus = :ticketStatus")
    Optional<List<Ticket>> findByConcertWithTicketStatus(@Param("concert") Concert concert, @Param("ticketStatus") TicketStatus ticketStatus);

    @Query("select count(t) from Ticket t where t.concert = :concert and t.ticketStatus = :ticketStatus and t.user.gender = :gender")
    int countTicketByConcertWithTicketStatusAndGender(@Param("concert") Concert concert, @Param("ticketStatus") TicketStatus ticketStatus, @Param("gender") Gender gender);

    @Query("select count(t) from Ticket t where  t.concert = :concert")
    int countTicketByConcert(@Param("concert") Concert concert);
}
