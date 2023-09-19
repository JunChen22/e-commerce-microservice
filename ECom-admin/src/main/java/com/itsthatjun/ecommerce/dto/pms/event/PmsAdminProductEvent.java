package com.itsthatjun.ecommerce.dto.pms.event;

import com.itsthatjun.ecommerce.mbg.model.Product;
import lombok.Getter;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class PmsAdminProductEvent {

    public enum Type {
        NEW_PRODUCT,
        UPDATE_PRODUCT,
        REMOVE_PRODUCT
    }

    private final Type eventType;
    private final Product product;
    private final Integer productId;
    private final ZonedDateTime eventCreatedAt;

    // Jackson needs it, (the library used for JSON serialization/deserialization)
    public PmsAdminProductEvent() {
        this.eventType = null;
        this.product = null;
        this.productId = null;
        this.eventCreatedAt = null;
    }

    public PmsAdminProductEvent(Type eventType, Product product, Integer productId) {
        this.eventType = eventType;
        this.product = product;
        this.productId = productId;
        this.eventCreatedAt = now();
    }
}
