package com.example.ticketing.domain.seat.entity;

import com.example.ticketing.domain.concert.entity.Concert;
import com.example.ticketing.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "seats",
        indexes = {
                @Index(name = "idx_seat_details_id", columnList = "seat_details_id"),
                @Index(name = "idx_seat_number", columnList = "seat_number") // seatNumber 인덱스 추가!
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id")
    private Concert concert;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_details_id")
    private SeatDetail seatDetail;

    private Boolean isAvailable;

    private int seatNumber;

    @Builder
    public Seat (
            Long id,
            Concert concert,
            SeatDetail seatDetail,
            boolean isAvailable,
            int seatNumber
    ) {
        this.id = id;
        this.concert = concert;
        this.seatDetail = seatDetail;
        this.isAvailable = isAvailable;
        this.seatNumber = seatNumber;
    }
}
