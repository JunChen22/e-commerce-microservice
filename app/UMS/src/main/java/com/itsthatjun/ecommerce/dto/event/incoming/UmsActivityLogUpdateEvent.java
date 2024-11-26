package com.itsthatjun.ecommerce.dto.event.incoming;

import com.itsthatjun.ecommerce.model.entity.MemberActivityLog;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.UUID;

import static java.time.ZonedDateTime.now;

@Getter
public class UmsActivityLogUpdateEvent {

    public enum Type {
        LOG_IN,
        LOG_OFF,
        FAILED_LOGIN,
        PASSWORD_CHANGE,
        SESSION_EXPIRED,
        TWO_FA_SUCCESS,
        TWO_FA_FAILED,
        ACCOUNT_LOCKOUT
    }

    private final Type eventType;
    private final UUID memberId;
    private final MemberActivityLog activityLog;
    private final ZonedDateTime eventCreatedAt;

    public UmsActivityLogUpdateEvent(Type eventType, UUID memberId, MemberActivityLog activityLog) {
        this.eventType = eventType;
        this.memberId = memberId;
        this.activityLog = activityLog;
        this.eventCreatedAt = now();
    }
}
