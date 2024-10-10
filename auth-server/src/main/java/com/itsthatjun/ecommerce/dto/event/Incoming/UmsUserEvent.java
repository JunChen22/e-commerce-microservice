package com.itsthatjun.ecommerce.dto.event.Incoming;

import com.itsthatjun.ecommerce.model.Member;
import lombok.Getter;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class UmsUserEvent {

    public enum Type {
        NEW_ACCOUNT,
        UPDATE_ACCOUNT_STATUS,
        UPDATE_ACCOUNT_INFO,
        DELETE_ACCOUNT
    }

    private final Type eventType;
    private final int userId;
    private final Member member;
    private final ZonedDateTime eventCreatedAt;

    public UmsUserEvent(Type eventType, int userId, Member member) {
        this.eventType = eventType;
        this.userId = userId;
        this.member = member;
        this.eventCreatedAt = now();
    }
}
