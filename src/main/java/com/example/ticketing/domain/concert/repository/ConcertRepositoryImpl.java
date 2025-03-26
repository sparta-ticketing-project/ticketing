package com.example.ticketing.domain.concert.repository;

import com.example.ticketing.domain.concert.entity.Concert;
import com.example.ticketing.domain.concert.entity.QConcert;
import com.example.ticketing.domain.concert.enums.ConcertType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.ticketing.domain.concert.entity.QConcert.concert;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Concert> findConcertsByKeyword(Pageable pageable, String concertName, String concertType) {

        List<Concert> concerts = queryFactory.selectFrom(concert)
                .where(searchByConcertType(concertType), searchByConcertName(concertName))
                .orderBy(concert.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory.select(concert.count())
                .from(concert)
                .where(searchByConcertType(concertType), searchByConcertName(concertName))
                .fetchOne();

        return new PageImpl<>(concerts, pageable, total);
    }

    private BooleanExpression searchByConcertName(String concertName){
        if(concertName == null){
            return null;
        }

        return concert.concertName.like("%" + concertName + "%");
    }

    private BooleanExpression searchByConcertType(String concertType){
        if(concertType == null){
            return null;
        }

        return concert.concertType.eq(ConcertType.of(concertType));
    }
}
