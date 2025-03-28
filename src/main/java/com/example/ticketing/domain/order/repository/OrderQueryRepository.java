package com.example.ticketing.domain.order.repository;

import com.example.ticketing.domain.order.dto.response.OrderListResponse;
import com.example.ticketing.domain.order.enums.OrderStatus;
import com.example.ticketing.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderQueryRepository {

    Page<OrderListResponse> findOrdersBy(User user, OrderStatus orderStatus, Pageable pageable);
}
