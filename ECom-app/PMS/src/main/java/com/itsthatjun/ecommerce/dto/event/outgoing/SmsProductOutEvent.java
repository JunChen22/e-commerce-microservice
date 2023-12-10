package com.itsthatjun.ecommerce.dto.event.outgoing;

import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.ProductSku;
import lombok.Getter;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class SmsProductOutEvent {

    public enum Type {
        NEW_PRODUCT,
        NEW_PRODUCT_SKU,
        UPDATE_PRODUCT,
        REMOVE_PRODUCT_SKU,
        DELETE_PRODUCT
    }

    private final OmsProductOutEvent.Type eventType;
    private final Product product;
    private final ProductSku productSku;
    private final ZonedDateTime eventCreatedAt;

    public SmsProductOutEvent() {
        this.eventType = null;
        this.product = null;
        this.productSku = null;
        this.eventCreatedAt = null;
    }

    public SmsProductOutEvent(OmsProductOutEvent.Type eventType, Product product, ProductSku productSku) {
        this.eventType = eventType;
        this.product = product;
        this.productSku = productSku;
        this.eventCreatedAt = now();
    }
}
