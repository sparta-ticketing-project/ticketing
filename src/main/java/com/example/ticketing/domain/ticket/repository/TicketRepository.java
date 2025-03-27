package com.example.ticketing.domain.ticket.repository;

import com.example.ticketing.domain.concert.entity.Concert;
import com.example.ticketing.domain.ticket.entity.Ticket;
import com.example.ticketing.domain.ticket.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("select t from Ticket t where t.concert = :concert and t.ticketStatus = :ticketStatus")
    Optional<Ticket> findByConcertWithTicketStatus(@Param("concert") Concert concert, @Param("ticketStatus") TicketStatus ticketStatus);

}
