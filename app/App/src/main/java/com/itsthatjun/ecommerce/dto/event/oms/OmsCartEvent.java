package com.itsthatjun.ecommerce.dto.event.oms;

import com.itsthatjun.ecommerce.dto.oms.model.CartItemDTO;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.UUID;

import static java.time.ZonedDateTime.now;

@Getter
public class OmsCartEvent {

    public enum Type {
        ADD_ONE,
        UPDATE,
        DELETE,
        CLEAR
    }

    private final Type eventType;
    private final UUID memberId;
    private final CartItemDTO cartItem;
    private final ZonedDateTime eventCreatedAt;

    public OmsCartEvent(Type eventType, UUID memberId, CartItemDTO cartItem) {
        this.eventType = eventType;
        this.memberId = memberId;
        this.cartItem = cartItem;
        this.eventCreatedAt = now();
    }
}