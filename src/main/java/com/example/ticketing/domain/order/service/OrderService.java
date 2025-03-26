package com.example.ticketing.domain.order.service;

import com.example.ticketing.domain.concert.entity.Concert;
import com.example.ticketing.domain.concert.repository.ConcertRepository;
import com.example.ticketing.domain.order.dto.request.CreateOrderRequest;
import com.example.ticketing.domain.order.dto.response.CreateOrderResponse;
import com.example.ticketing.domain.order.entity.Order;
import com.example.ticketing.domain.order.enums.OrderStatus;
import com.example.ticketing.domain.order.repository.OrderRepository;
import com.example.ticketing.domain.ticket.entity.Ticket;
import com.example.ticketing.domain.ticket.service.TicketService;
import com.example.ticketing.domain.user.entity.User;
import com.example.ticketing.domain.user.repository.UserRepository;
import com.example.ticketing.global.exception.CustomException;
import com.example.ticketing.global.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ConcertRepository concertRepository;
    private final TicketService ticketService;

    @Transactional
    public CreateOrderResponse createOrder(Long userId, Long concertId, CreateOrderRequest request) {
        User user = getUserById(userId);
        Concert concert = getConcertById(concertId);

        validateConcertDate(concert.getConcertDate());
        validateTicketingDate(concert.getTicketingDate());
        ticketService.validateUserTicketLimit(concert, user, request.getSeatIds().size());

        Order order = orderRepository.save(
                Order.builder()
                        .user(user)
                        .concert(concert)
                        .orderStatus(OrderStatus.PENDING)
                        .totalPrice(0)
                        .build()
        );

        List<Ticket> tickets = ticketService.issueTickets(user, concert, order, request.getSeatIds());
        int totalPrice = getTotalPriceFrom(tickets);

        useUserPoint(user, totalPrice);

        order.updateTotalPrice(totalPrice);
        order.updateOrderStatus(OrderStatus.COMPLETED);

        return CreateOrderResponse.of(concert, order, tickets);
    }

    private void useUserPoint(User user, int amount) {
        if (user.getPoint() < amount) {
            throw new CustomException(ExceptionType.ORDER_POINT_NOT_ENOUGH);
        }

        user.deductPoint(amount);
    }

    private void validateConcertDate(LocalDateTime concertDate) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(concertDate)) {
            throw new CustomException(ExceptionType.ORDER_INVALID_REQUEST, "예매 가능 시간이 지났습니다.");
        }
    }

    private void validateTicketingDate(LocalDateTime ticketingDate) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(ticketingDate)) {
            throw new CustomException(ExceptionType.ORDER_INVALID_REQUEST, "예매 시작 전 주문을 요청할 수 없습니다.");
        }
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(()-> new CustomException(ExceptionType.USER_NOT_FOUND));
    }

    private Concert getConcertById(Long concertId) {
        return concertRepository.findById(concertId)
                .orElseThrow(()-> new CustomException(ExceptionType.CONCERT_NOT_FOUND));
    }


    private int getTotalPriceFrom(List<Ticket> tickets) {
        return tickets.stream()
                .mapToInt(Ticket::getPrice)
                .sum();
    }
}
