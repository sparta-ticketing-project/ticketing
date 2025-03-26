package com.example.ticketing.domain.order.repository;

import com.example.ticketing.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
