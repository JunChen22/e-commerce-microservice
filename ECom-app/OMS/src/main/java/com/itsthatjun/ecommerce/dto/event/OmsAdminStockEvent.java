package com.itsthatjun.ecommerce.dto.event;

import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.ProductSkuStock;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
public class OmsAdminStockEvent {

    public enum Type {
        NEW_PRODUCT,
        UPDATE_STOCK,
        REMOVE_PRODUCT,
        UPDATE_PRICE
    }

    private final Type eventType;
    private final Product product;
    private final ProductSkuStock skuStock;
    private final ZonedDateTime eventCreatedAt;

    public OmsAdminStockEvent() {
        this.eventType = null;
        this.product = null;
        this.skuStock = null;
        this.eventCreatedAt = null;
    }

    public OmsAdminStockEvent(Type eventType, Product product, ProductSkuStock skuStock, ZonedDateTime eventCreatedAt) {
        this.eventType = eventType;
        this.product = product;
        this.skuStock = skuStock;
        this.eventCreatedAt = eventCreatedAt;
    }
}
