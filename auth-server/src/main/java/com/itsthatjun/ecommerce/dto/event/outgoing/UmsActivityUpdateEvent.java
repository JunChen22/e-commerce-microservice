package com.itsthatjun.ecommerce.dto.event.outgoing;

import com.itsthatjun.ecommerce.model.entity.MemberActivityLog;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.UUID;

import static java.time.ZonedDateTime.now;

@Getter
public class UmsActivityUpdateEvent {

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
    private final MemberActivityLog loginLog;
    private final ZonedDateTime eventCreatedAt;

    public UmsActivityUpdateEvent(Type eventType, UUID memberId, MemberActivityLog loginLog) {
        this.eventType = eventType;
        this.memberId = memberId;
        this.loginLog = loginLog;
        this.eventCreatedAt = now();
    }
}
