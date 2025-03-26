package com.example.ticketing.domain.seat.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSeatDetail is a Querydsl query type for SeatDetail
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSeatDetail extends EntityPathBase<SeatDetail> {

    private static final long serialVersionUID = -460571399L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSeatDetail seatDetail = new QSeatDetail("seatDetail");

    public final com.example.ticketing.global.entity.QBaseTimeEntity _super = new com.example.ticketing.global.entity.QBaseTimeEntity(this);

    public final NumberPath<Integer> availableSeatCount = createNumber("availableSeatCount", Integer.class);

    public final com.example.ticketing.domain.concert.entity.QConcert concert;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final NumberPath<Integer> price = createNumber("price", Integer.class);

    public final EnumPath<com.example.ticketing.domain.seat.enums.SeatType> seatType = createEnum("seatType", com.example.ticketing.domain.seat.enums.SeatType.class);

    public final NumberPath<Integer> totalSeatCount = createNumber("totalSeatCount", Integer.class);

    public QSeatDetail(String variable) {
        this(SeatDetail.class, forVariable(variable), INITS);
    }

    public QSeatDetail(Path<? extends SeatDetail> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSeatDetail(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSeatDetail(PathMetadata metadata, PathInits inits) {
        this(SeatDetail.class, metadata, inits);
    }

    public QSeatDetail(Class<? extends SeatDetail> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.concert = inits.isInitialized("concert") ? new com.example.ticketing.domain.concert.entity.QConcert(forProperty("concert"), inits.get("concert")) : null;
    }

}

