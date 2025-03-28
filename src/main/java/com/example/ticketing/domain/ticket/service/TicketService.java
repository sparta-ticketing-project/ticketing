package com.example.ticketing.domain.ticket.service;

import com.example.ticketing.domain.concert.entity.Concert;
import com.example.ticketing.domain.order.entity.Order;
import com.example.ticketing.domain.seat.entity.Seat;
import com.example.ticketing.domain.seat.repository.SeatDetailRepository;
import com.example.ticketing.domain.seat.repository.SeatRepository;
import com.example.ticketing.domain.ticket.entity.Ticket;
import com.example.ticketing.domain.ticket.enums.TicketStatus;
import com.example.ticketing.domain.ticket.repository.TicketRepository;
import com.example.ticketing.domain.user.entity.User;
import com.example.ticketing.global.exception.CustomException;
import com.example.ticketing.global.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final SeatDetailRepository seatDetailRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public List<Ticket> issueTickets(User user, Concert concert, Order order, List<Long> seatIds) {
        List<Seat> seats = getSeats(seatIds);

        validateSeatsBelongToSameConcert(seats);
        validateAllSeatsAreAvailable(seats);

        List<Ticket> tickets = seats.stream()
                .map( seat -> createTicket(user, concert, order, seat))
                .toList();

        return ticketRepository.saveAll(tickets);
    }

    public List<Ticket> getTicketsByOrder(Order order) {
        return ticketRepository.findAllWithSeatAndSeatDetailByOrder(order);
    }

    public void cancelTickets(Order order) {
        List<Ticket> tickets = getTicketsByOrder(order);

        for (Ticket ticket : tickets) {
            ticket.markAsUnavailable();
            ticket.getSeat().markAsAvailable();
            ticket.getSeat().getSeatDetail().increaseAvailableSeatCount(1);
            ticket.getConcert().increaseAvailableSeatCount(1);
        }
    }

    public void validateUserTicketLimit(Concert concert, User user, int requestedCount) {
        int maxAllowed = concert.getMaxTicketPerUser();

        if (requestedCount > maxAllowed) {
            throw new CustomException(
                    ExceptionType.ORDER_TICKET_LIMIT_EXCEEDED,
                    String.format("1인당 최대 %d개의 좌석만 예매 가능합니다.", maxAllowed)
            );
        }

        int alreadyReserved = ticketRepository.countByConcertAndUserAndTicketStatus(concert, user, TicketStatus.AVAILABLE);
        int totalAfterRequest = alreadyReserved + requestedCount;

        if (totalAfterRequest > maxAllowed) {
            int remaining = maxAllowed - alreadyReserved;
            throw new CustomException(
                    ExceptionType.ORDER_TICKET_LIMIT_EXCEEDED,
                    String.format("이미 %d개의 좌석을 예매하셨습니다. 남은 가능 수량: %d개.", alreadyReserved, remaining)
            );
        }
    }

    private void validateSeatsBelongToSameConcert(List<Seat> seats) {
        Set<Long> concertIds = seats.stream()
                .map(seat -> seat.getConcert().getId())
                .collect(Collectors.toSet());

        if (concertIds.size() > 1) {
            throw new CustomException(ExceptionType.ORDER_CONCERT_MISMATCH);
        }
    }

    private void validateAllSeatsAreAvailable(List<Seat> seats) {
        for (Seat seat : seats) {
            if (!seat.isAvailable()) {
                throw new CustomException(ExceptionType.ORDER_SEAT_ALREADY_TAKEN);
            }
        }
    }

    private Ticket createTicket(User user, Concert concert, Order order, Seat seat) {
        seat.markAsUnavailable();
        seat.getSeatDetail().decreaseAvailableSeatCount(1);
        concert.decreaseAvailableSeatCount(1);
        return Ticket.builder()
                .user(user)
                .order(order)
                .concert(concert)
                .seat(seat)
                .price(seat.getSeatDetail().getPrice())
                .ticketStatus(TicketStatus.AVAILABLE)
                .build();
    }

    private List<Seat> getSeats(List<Long> seatIds) {
        List<Seat> seats = seatRepository.findAllWithSeatDetailAndConcertByIdIn(seatIds);
        if (seats.size() != seatIds.size()) {
            throw new CustomException(ExceptionType.SEAT_NOT_FOUND);
        }
        return seats;
    }
}
