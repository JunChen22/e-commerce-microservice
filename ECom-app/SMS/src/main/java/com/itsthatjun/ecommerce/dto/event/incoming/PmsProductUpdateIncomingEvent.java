package com.itsthatjun.ecommerce.dto.event.incoming;

import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.ProductSku;
import lombok.Getter;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class PmsProductUpdateIncomingEvent {

    public enum Type {
        NEW_PRODUCT,
        NEW_PRODUCT_SKU,
        UPDATE_PRODUCT,     // Logic is already done in PMS, just need update current information.
        UPDATE_PRODUCT_STATUS,
        REMOVE_PRODUCT_SKU,
        REMOVE_PRODUCT
    }

    private final Type eventType;
    private final Product product;
    private final ProductSku productSku;
    private final ZonedDateTime eventCreatedAt;

    // Jackson needs it, (the library used for JSON serialization/deserialization)
    public PmsProductUpdateIncomingEvent() {
        this.eventType = null;
        this.product = null;
        this.productSku = null;
        this.eventCreatedAt = null;
    }

    public PmsProductUpdateIncomingEvent(Type eventType, Product product, ProductSku productSku) {
        this.eventType = eventType;
        this.product = product;
        this.productSku = productSku;
        this.eventCreatedAt = now();
    }
}
