package com.itsthatjun.ecommerce.dto.event.outgoing;

import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.ProductSku;
import lombok.Getter;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class OmsProductOutEvent {

    public enum Type {
        NEW_PRODUCT,
        NEW_PRODUCT_SKU,
        UPDATE_PRODUCT,
        UPDATE_PRODUCT_STATUS,
        REMOVE_PRODUCT_SKU,
        REMOVE_PRODUCT
    }

    private final Type eventType;
    private final Product product;
    private final ProductSku productSku;
    private final ZonedDateTime eventCreatedAt;

    // Jackson needs it, (the library used for JSON serialization/deserialization)
    public OmsProductOutEvent() {
        this.eventType = null;
        this.product = null;
        this.productSku = null;
        this.eventCreatedAt = null;
    }

    public OmsProductOutEvent(Type eventType, Product product, ProductSku productSku) {
        this.eventType = eventType;
        this.product = product;
        this.productSku = productSku;
        this.eventCreatedAt = now();
    }
}
