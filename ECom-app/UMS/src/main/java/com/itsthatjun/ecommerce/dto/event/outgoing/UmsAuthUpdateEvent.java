package com.itsthatjun.ecommerce.dto.event.outgoing;

import com.itsthatjun.ecommerce.dto.MemberDetail;
import com.itsthatjun.ecommerce.dto.event.incoming.UmsUserEvent;
import com.itsthatjun.ecommerce.mbg.model.Member;
import com.itsthatjun.ecommerce.mbg.model.MemberLoginLog;
import lombok.Getter;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class UmsAuthUpdateEvent {

    public enum Type {
        NEW_ACCOUNT,
        UPDATE_ACCOUNT_INFO,
        UPDATE_STATUS,
        DELETE_ACCOUNT
    }

    private final Type eventType;
    private final int userId;
    private final Member member;
    private final ZonedDateTime eventCreatedAt;

    // Jackson needs it, (the library used for JSON serialization/deserialization)
    public UmsAuthUpdateEvent() {
        this.eventType = null;
        this.userId = 0;
        this.member = null;
        this.eventCreatedAt = null;
    }

    public UmsAuthUpdateEvent(Type eventType, int userId, Member member) {
        this.eventType = eventType;
        this.userId = userId;
        this.member = member;
        this.eventCreatedAt = now();
    }
}