package com.example.ticketing.domain.ticket.entity;

import com.example.ticketing.domain.concert.entity.Concert;
import com.example.ticketing.domain.order.entity.Order;
import com.example.ticketing.domain.seat.entity.Seat;
import com.example.ticketing.domain.ticket.enums.TicketStatus;
import com.example.ticketing.domain.user.entity.User;
import com.example.ticketing.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tickets")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticket extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id")
    private Concert concert;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    private int price;

    @Enumerated(EnumType.STRING)
    private TicketStatus ticketStatus;

    @Builder
    public Ticket(User user, Order order, Concert concert, Seat seat, int price, TicketStatus ticketStatus) {
        this.user = user;
        this.order = order;
        this.concert = concert;
        this.seat = seat;
        this.price = price;
        this.ticketStatus = ticketStatus;
    }

    public void markAsUnavailable() {
        this.ticketStatus = TicketStatus.UNAVAILABLE;
    }
}
