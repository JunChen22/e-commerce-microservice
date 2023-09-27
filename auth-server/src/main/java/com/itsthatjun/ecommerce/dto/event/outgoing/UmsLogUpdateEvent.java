package com.itsthatjun.ecommerce.dto.event.outgoing;

import com.itsthatjun.ecommerce.mbg.model.Member;
import com.itsthatjun.ecommerce.mbg.model.MemberLoginLog;
import lombok.Getter;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class UmsLogUpdateEvent {

    public enum Type {
        New_LOGIN,
        LOG_OFF
    }

    private final Type eventType;
    private final int userId;
    private final MemberLoginLog loginLog;
    private final ZonedDateTime eventCreatedAt;

    // Jackson needs it, (the library used for JSON serialization/deserialization)
    public UmsLogUpdateEvent() {
        this.eventType = null;
        this.userId = 0;
        this.loginLog = null;
        this.eventCreatedAt = null;
    }

    public UmsLogUpdateEvent(Type eventType, int userId, MemberLoginLog loginLog) {
        this.eventType = eventType;
        this.userId = userId;
        this.loginLog = loginLog;
        this.eventCreatedAt = now();
    }
}
