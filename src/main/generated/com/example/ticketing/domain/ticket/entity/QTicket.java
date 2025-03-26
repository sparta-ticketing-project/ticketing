package com.example.ticketing.domain.ticket.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTicket is a Querydsl query type for Ticket
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTicket extends EntityPathBase<Ticket> {

    private static final long serialVersionUID = -222546090L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTicket ticket = new QTicket("ticket");

    public final com.example.ticketing.global.entity.QBaseTimeEntity _super = new com.example.ticketing.global.entity.QBaseTimeEntity(this);

    public final com.example.ticketing.domain.concert.entity.QConcert concert;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final com.example.ticketing.domain.seat.entity.QSeat seat;

    public final EnumPath<com.example.ticketing.domain.ticket.enums.TicketStatus> ticketStatus = createEnum("ticketStatus", com.example.ticketing.domain.ticket.enums.TicketStatus.class);

    public final com.example.ticketing.domain.user.entity.QUser user;

    public QTicket(String variable) {
        this(Ticket.class, forVariable(variable), INITS);
    }

    public QTicket(Path<? extends Ticket> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTicket(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTicket(PathMetadata metadata, PathInits inits) {
        this(Ticket.class, metadata, inits);
    }

    public QTicket(Class<? extends Ticket> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.concert = inits.isInitialized("concert") ? new com.example.ticketing.domain.concert.entity.QConcert(forProperty("concert"), inits.get("concert")) : null;
        this.seat = inits.isInitialized("seat") ? new com.example.ticketing.domain.seat.entity.QSeat(forProperty("seat"), inits.get("seat")) : null;
        this.user = inits.isInitialized("user") ? new com.example.ticketing.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

