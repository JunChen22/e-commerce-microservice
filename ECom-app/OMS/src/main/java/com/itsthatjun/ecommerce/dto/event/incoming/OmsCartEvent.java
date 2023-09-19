package com.itsthatjun.ecommerce.dto.event.incoming;

import static java.time.ZonedDateTime.now;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
public class OmsCartEvent<K, T> {

    // It's getting big because it's for 3 service at once, will get smaller if break it down
    public enum Type {
        ADD_ONE,
        ADD_ALL,
        UPDATE,
        DELETE,
        CLEAR
    }

    private final Type eventType;
    private final K key;
    private final List<T> data;
    private final int quantity;
    private final int cartItemId;
    private final ZonedDateTime eventCreatedAt;

    // Jackson needs it, (the library used for JSON serialization/deserialization)
    public OmsCartEvent() {
        this.eventType = null;
        this.key = null;
        this.data = null;
        this.quantity = 0;
        this.cartItemId = 0;
        this.eventCreatedAt = null;
    }

    public OmsCartEvent(Type eventType, K key, List<T> data) {
        this(eventType, key, data, 1,-1);
    }

    public OmsCartEvent(Type eventType, K key, List<T> data, int quantity, int cartItemId) {
        this.eventType = eventType;
        this.key = key;
        this.data = data;
        this.quantity = quantity;
        this.cartItemId = cartItemId;
        this.eventCreatedAt = now();
    }

    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    public ZonedDateTime getEventCreatedAt() {
        return eventCreatedAt;
    }
}