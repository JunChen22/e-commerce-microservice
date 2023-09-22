package com.itsthatjun.ecommerce.dto.event.oms;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import com.itsthatjun.ecommerce.mbg.model.CartItem;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

import static java.time.ZonedDateTime.now;

@Getter
public class OmsCartEvent {

    public enum Type {
        ADD_ONE,
        ADD_ALL,
        UPDATE,
        DELETE,
        CLEAR
    }

    private final Type eventType;
    private final Integer userId;
    private final List<CartItem> cartItemList;
    private final int quantity;
    private final int cartItemId;
    private final ZonedDateTime eventCreatedAt;

    public OmsCartEvent() {
        this.eventType = null;
        this.userId = null;
        this.cartItemList = null;
        this.quantity = 0;
        this.cartItemId = 0;
        this.eventCreatedAt = null;
    }

    public OmsCartEvent(Type eventType, Integer userId, List<CartItem> cartItemList) {
        this(eventType, userId, cartItemList, 1,-1);
    }

    public OmsCartEvent(Type eventType, Integer userId, List<CartItem> cartItemList, int quantity, int cartItemId) {
        this.eventType = eventType;
        this.userId = userId;
        this.cartItemList = cartItemList;
        this.quantity = quantity;
        this.cartItemId = cartItemId;
        this.eventCreatedAt = now();
    }

    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    public ZonedDateTime getEventCreatedAt() {
        return eventCreatedAt;
    }
}