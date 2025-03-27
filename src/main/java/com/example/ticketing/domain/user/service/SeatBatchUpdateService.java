package com.example.ticketing.domain.user.service;

import com.example.ticketing.domain.seat.entity.Seat;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatBatchUpdateService {

    private final JdbcTemplate jdbcTemplate;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void seatBatchUpdate(List<Seat> seats, boolean tochange) {
        String currentTransactionName = TransactionSynchronizationManager.getCurrentTransactionName();
        System.out.println(currentTransactionName);
        boolean isNewTransaction = TransactionAspectSupport.currentTransactionStatus().isNewTransaction();
        System.out.println("Is new transaction? " + isNewTransaction);
        String sql = "UPDATE seats SET is_available = ?, modified_at = ? WHERE id = ?";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Seat seat = seats.get(i); // Seat 객체를 사용하여 id와 상태를 업데이트
                ps.setBoolean(1, tochange);  // 1번째 파라미터: is_available
                ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));  // 2번째 파라미터: modified_at
                ps.setLong(3, seat.getId());  // 3번째 파라미터: id
            }

            @Override
            public int getBatchSize() {
                return seats.size();
            }
        });
    }


}
