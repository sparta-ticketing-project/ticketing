package com.example.ticketing.domain.ticket.repository;

import com.example.ticketing.domain.concert.entity.Concert;
import com.example.ticketing.domain.order.entity.Order;
import com.example.ticketing.domain.ticket.entity.Ticket;
import com.example.ticketing.domain.ticket.enums.TicketStatus;
import com.example.ticketing.domain.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @EntityGraph(attributePaths = {"seat", "seat.seatDetail"})
    List<Ticket> findAllWithSeatAndSeatDetailByOrder(Order order);
    int countByConcertAndUserAndTicketStatus(Concert concert, User user, TicketStatus ticketStatus);
}
