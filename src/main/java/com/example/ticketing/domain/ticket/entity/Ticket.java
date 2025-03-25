package com.example.ticketing.domain.ticket.entity;

import com.example.ticketing.domain.concert.entity.Concert;
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
    @JoinColumn(name = "concert_id")
    private Concert concert;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @Enumerated(EnumType.STRING)
    private TicketStatus ticketStatus;

    @Builder
    public Ticket(Concert concert, User user, Seat seat, TicketStatus ticketStatus) {
        this.concert = concert;
        this.user = user;
        this.seat = seat;
        this.ticketStatus = ticketStatus;
    }
}
