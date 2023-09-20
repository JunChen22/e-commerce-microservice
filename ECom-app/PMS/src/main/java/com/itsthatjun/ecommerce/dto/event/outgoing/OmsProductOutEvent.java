package com.itsthatjun.ecommerce.dto.event.outgoing;

import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.ProductSku;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

import static java.time.ZonedDateTime.now;

@Getter
public class OmsProductOutEvent {

    public enum Type {
        NEW_PRODUCT,
        NEW_PRODUCT_SKU,
        UPDATE_STOCK,
        UPDATE_PRODUCT_PRICE,
        UPDATE_PRODUCT_STATUS,
        UPDATE_PRODUCT_SKU_STATUS,
        REMOVE_PRODUCT_SKU,
        DELETE_PRODUCT
    }

    private final Type eventType;
    private final Product product;
    private final List<ProductSku> productSkuList;
    private final ZonedDateTime eventCreatedAt;

    // Jackson needs it, (the library used for JSON serialization/deserialization)
    public OmsProductOutEvent() {
        this.eventType = null;
        this.product = null;
        this.productSkuList = null;
        this.eventCreatedAt = null;
    }

    public OmsProductOutEvent(Type eventType, Product product, List<ProductSku> productSkuList) {
        this.eventType = eventType;
        this.product = product;
        this.productSkuList = productSkuList;
        this.eventCreatedAt = now();
    }
}
