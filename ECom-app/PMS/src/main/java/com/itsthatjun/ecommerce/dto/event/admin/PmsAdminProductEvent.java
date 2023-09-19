package com.itsthatjun.ecommerce.dto.event.admin;

import com.itsthatjun.ecommerce.dto.ProductDetail;
import com.itsthatjun.ecommerce.mbg.model.Product;
import lombok.Getter;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class PmsAdminProductEvent {

    public enum Type {
        NEW_PRODUCT,
        UPDATE_STOCK,
        UPDATE_PRODUCT_PRICE,
        REMOVE_PRODUCT
    }

    private final Type eventType;
    private final ProductDetail productDetail;
    private final ZonedDateTime eventCreatedAt;

    // Jackson needs it, (the library used for JSON serialization/deserialization)
    public PmsAdminProductEvent() {
        this.eventType = null;
        this.productDetail = null;
        this.eventCreatedAt = null;
    }

    public PmsAdminProductEvent(Type eventType, ProductDetail productDetail) {
        this.eventType = eventType;
        this.productDetail = productDetail;
        this.eventCreatedAt = now();
    }
}
