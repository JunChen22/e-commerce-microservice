package com.itsthatjun.ecommerce.dto.event.outgoing;

import com.itsthatjun.ecommerce.model.entity.Member;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.UUID;

import static java.time.ZonedDateTime.now;

@Getter
public class UmsAuthUpdateEvent {

    public enum Type {
        NEW_ACCOUNT,
        UPDATE_ACCOUNT_INFO,
        UPDATE_ACCOUNT_STATUS,
        DELETE_ACCOUNT
    }

    private final Type eventType;
    private final UUID memberId;
    private final Member member;
    private final ZonedDateTime eventCreatedAt;

    public UmsAuthUpdateEvent(Type eventType, UUID memberId, Member member) {
        this.eventType = eventType;
        this.memberId = memberId;
        this.member = member;
        this.eventCreatedAt = now();
    }
}