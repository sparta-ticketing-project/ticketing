package com.example.ticketing.domain.seat.entity;

import com.example.ticketing.domain.concert.entity.Concert;
import com.example.ticketing.domain.seat.enums.SeatType;
import com.example.ticketing.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "seat_details")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SeatDetail extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id")
    private Concert concert;

    @Enumerated(EnumType.STRING)
    private SeatType seatType;

    private int price;

    private int totalSeatCount;

    private int availableSeatCount;

    public void setAvailableSeatCount(int availableSeatCount) {
        this.availableSeatCount = availableSeatCount;
    }

    public void updatePrice(int price) {
        this.price = price;
    }

    @Builder
    public SeatDetail (
            Long id,
            Concert concert,
            SeatType seatType,
            int price,
            int totalSeatCount,
            int availableSeatCount
    ) {
        this.id = id;
        this.concert = concert;
        this.seatType = seatType;
        this.price = price;
        this.totalSeatCount = totalSeatCount;
        this.availableSeatCount = availableSeatCount;
    }

    public void increaseAvailableSeatCount(int amount) {
        this.availableSeatCount += amount;
    }
}
