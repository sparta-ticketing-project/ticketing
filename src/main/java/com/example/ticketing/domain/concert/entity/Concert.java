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

    private Long viewCount = 0L;

    private Boolean isDeleted;

    public void updateConcert(
            String concertName,
            LocalDateTime concertDate,
            LocalDateTime ticketingDate,
            ConcertType concertType,
            Integer maxTicketPerUser) {
        if (concertName != null) {
            this.concertName = concertName;
        }

        if (concertDate != null) {
            this.concertDate = concertDate;
        }

        if (ticketingDate != null) {
            this.ticketingDate = ticketingDate;
        }

        if (concertType != null) {
            this.concertType = concertType;
        }

        if (maxTicketPerUser != null) {
            this.maxTicketPerUser = maxTicketPerUser;
        }
    }

    public void setTotalSeatCount(int totalSeatCount) {
        this.totalSeatCount = totalSeatCount;
    }

    public void setAvailableSeatCount(int availableSeatCount) {
        this.availableSeatCount = availableSeatCount;
    }

    public void setIsDeleted(Boolean isDeleted) {this.isDeleted = isDeleted;}

    @Builder
    public Concert (
            User user,
            String concertName,
            LocalDateTime concertDate,
            LocalDateTime ticketingDate,
            ConcertType concertType,
            int maxTicketPerUser,
            Boolean isDeleted
    ) {
        this.user = user;
        this.concertName = concertName;
        this.concertDate = concertDate;
        this.ticketingDate = ticketingDate;
        this.concertType = concertType;
        this.maxTicketPerUser = maxTicketPerUser;
        this.isDeleted = isDeleted;
    }

    public void increaseAvailableSeatCount(int count) {
        this.availableSeatCount += count;
    }

    public void decreaseAvailableSeatCount(int count) {
        this.availableSeatCount -= count;
    }

    // 테스트 용도 지워야 된다.
    public Concert(Long viewCount, ConcertType concertType){
        this.viewCount = viewCount;
        this.concertType = concertType;
    }
}
