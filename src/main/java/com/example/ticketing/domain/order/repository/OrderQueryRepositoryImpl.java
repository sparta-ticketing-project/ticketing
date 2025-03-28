package com.example.ticketing.domain.order.repository;

import com.example.ticketing.domain.order.dto.response.OrderListResponse;
import com.example.ticketing.domain.order.enums.OrderStatus;
import com.example.ticketing.domain.ticket.dto.response.TicketListResponse;
import com.example.ticketing.domain.user.entity.User;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;

import static com.example.ticketing.domain.concert.entity.QConcert.concert;
import static com.example.ticketing.domain.order.entity.QOrder.order;
import static com.example.ticketing.domain.seat.entity.QSeat.seat;
import static com.example.ticketing.domain.ticket.entity.QTicket.ticket;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepositoryImpl implements OrderQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<OrderListResponse> findOrdersBy(User user, OrderStatus orderStatus, Pageable pageable) {

        // 1. 대상 Order ID 전부 조회
        List<Long> orderIds = queryFactory
                .select(order.id)
                .distinct()
                .from(order)
                .where(
                        order.user.eq(user),
                        order.orderStatus.eq(orderStatus)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (orderIds.isEmpty()) {
            return Page.empty(pageable);
        }

        // Step 2: 본 데이터 조회 - flat row
        List<Tuple> rows = queryFactory
                .select(
                        order.id,
                        order.createdAt,
                        order.orderStatus,
                        order.totalPrice,
                        concert.id,
                        concert.concertName,
                        concert.concertDate,
                        ticket.id,
                        ticket.ticketStatus,
                        seat.id
                )
                .from(ticket)
                .join(ticket.order, order)
                .join(ticket.concert, concert)
                .join(ticket.seat, seat)
                .where(order.id.in(orderIds))
                .fetch();

        // 3. 직접 OrderId 별로 그룹핑
        Map<Long, OrderListResponse> resultMap = new LinkedHashMap<>();

        for (Tuple row : rows) {
            Long orderId = row.get(order.id);
            OrderListResponse orderDto = resultMap.get(orderId);

            if (orderDto == null) {
                orderDto = OrderListResponse.builder()
                        .orderId(orderId)
                        .createdAt(row.get(order.createdAt))
                        .orderStatus(row.get(order.orderStatus))
                        .totalPrice(row.get(order.totalPrice))
                        .concertId(row.get(concert.id))
                        .concertName(row.get(concert.concertName))
                        .concertDate(row.get(concert.concertDate))
                        .tickets(new ArrayList<>())
                        .build();

                resultMap.put(orderId, orderDto);
            }

            TicketListResponse ticketDto = new TicketListResponse(
                    row.get(ticket.id),
                    row.get(seat.id)
            );

            orderDto.getTickets().add(ticketDto);
        }

        // 4. 페이징 처리를 위한 전체 개수 쿼리
        long total = Optional.ofNullable(
                queryFactory
                        .select(order.count())
                        .from(order)
                        .where(
                                order.user.eq(user),
                                order.orderStatus.eq(orderStatus)
                        )
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<> (new ArrayList<>(resultMap.values()), pageable, total);
    }
}
