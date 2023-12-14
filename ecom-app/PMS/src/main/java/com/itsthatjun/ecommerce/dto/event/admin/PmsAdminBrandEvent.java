package com.itsthatjun.ecommerce.dto.event.admin;

import com.itsthatjun.ecommerce.mbg.model.Brand;
import lombok.Getter;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class PmsAdminBrandEvent {

    public enum Type {
        CREATE,
        UPDATE,
        DELETE
    }

    private final Type eventType;
    private final Brand brand;
    private final String operator;
    private final ZonedDateTime eventCreatedAt;

    public PmsAdminBrandEvent(Type eventType, Brand brand, String operator) {
        this.eventType = eventType;
        this.brand = brand;
        this.operator = operator;
        this.eventCreatedAt = now();
    }
}
