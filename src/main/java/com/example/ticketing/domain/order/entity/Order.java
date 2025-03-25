package com.example.ticketing.domain.order.entity;

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

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private int totalPrice;

    @Builder
    public Order(User user, OrderStatus orderStatus, int totalPrice) {
        this.user = user;
        this.orderStatus = orderStatus;
        this.totalPrice = totalPrice;
    }
}
