package com.example.ticketing.domain.concert.entity;

import com.example.ticketing.domain.concert.enums.ConcertType;
import com.example.ticketing.domain.user.entity.User;
import com.example.ticketing.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "concerts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Concert extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String concertName;

    private LocalDateTime concertDate;

    private LocalDateTime ticketingDate;

    @Enumerated(EnumType.STRING)
    private ConcertType concertType;

    private int maxTicketPerUser;

    private int totalSeatCount;

    private int availableSeatCount;

    @Builder
    public Concert (
            String concertName,
            LocalDateTime concertDate,
            LocalDateTime ticketingDate,
            ConcertType concertType,
            int maxTicketPerUser
    ) {
        this.concertName = concertName;
        this.concertDate = concertDate;
        this.ticketingDate = ticketingDate;
        this.concertType = concertType;
        this.maxTicketPerUser = maxTicketPerUser;
    }
}
