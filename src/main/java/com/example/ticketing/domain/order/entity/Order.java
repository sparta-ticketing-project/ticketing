package com.example.ticketing.domain.order.entity;

import com.example.ticketing.domain.concert.entity.Concert;
import com.example.ticketing.domain.order.enums.OrderStatus;
import com.example.ticketing.domain.user.entity.User;
import com.example.ticketing.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "concert_id")
    private Concert concert;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private int totalPrice;

    @Builder
    public Order(User user, Concert concert, OrderStatus orderStatus, int totalPrice) {
        this.user = user;
        this.concert = concert;
        this.orderStatus = orderStatus;
        this.totalPrice = totalPrice;
    }

    public void updateTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void updateOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
