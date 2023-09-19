package com.itsthatjun.ecommerce.dto.event.incoming;

import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.ProductSku;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

import static java.time.ZonedDateTime.now;

@Getter
public class PmsProductUpdateIncomingEvent {

    public enum Type {
        NEW_PRODUCT,
        NEW_PRODUCT_SKU,
        UPDATE_PRODUCT,
        REMOVE_PRODUCT,
        REMOVE_PRODUCT_SKU
    }

    private final Type eventType;
    private final Product product;
    private final List<ProductSku> productSkuList;
    private final ZonedDateTime eventCreatedAt;

    public PmsProductUpdateIncomingEvent() {
        this.eventType = null;
        this.product = null;
        this.productSkuList = null;
        this.eventCreatedAt = null;
    }

    public PmsProductUpdateIncomingEvent(Type eventType, Product product, List<ProductSku> productSkuList) {
        this.eventType = eventType;
        this.product = product;
        this.productSkuList = productSkuList;
        this.eventCreatedAt = now();
    }
}
