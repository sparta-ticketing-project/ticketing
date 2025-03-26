package com.example.ticketing.domain.concert.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QConcert is a Querydsl query type for Concert
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QConcert extends EntityPathBase<Concert> {

    private static final long serialVersionUID = -1226588508L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QConcert concert = new QConcert("concert");

    public final com.example.ticketing.global.entity.QBaseTimeEntity _super = new com.example.ticketing.global.entity.QBaseTimeEntity(this);

    public final NumberPath<Integer> availableSeatCount = createNumber("availableSeatCount", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> concertDate = createDateTime("concertDate", java.time.LocalDateTime.class);

    public final StringPath concertName = createString("concertName");

    public final EnumPath<com.example.ticketing.domain.concert.enums.ConcertType> concertType = createEnum("concertType", com.example.ticketing.domain.concert.enums.ConcertType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> maxTicketPerUser = createNumber("maxTicketPerUser", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final DateTimePath<java.time.LocalDateTime> ticketingDate = createDateTime("ticketingDate", java.time.LocalDateTime.class);

    public final NumberPath<Integer> totalSeatCount = createNumber("totalSeatCount", Integer.class);

    public final com.example.ticketing.domain.user.entity.QUser user;

    public final NumberPath<Long> viewCount = createNumber("viewCount", Long.class);

    public QConcert(String variable) {
        this(Concert.class, forVariable(variable), INITS);
    }

    public QConcert(Path<? extends Concert> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QConcert(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QConcert(PathMetadata metadata, PathInits inits) {
        this(Concert.class, metadata, inits);
    }

    public QConcert(Class<? extends Concert> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.example.ticketing.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

