package com.itsthatjun.ecommerce.dto.oms;

public enum OrderStatus {

    WAITING_FOR_PAYMENT(0),
    FULFILLING(1),
    SEND(2),
    COMPLETE(3),
    CLOSED(4),
    INVALID(5);

    private final int code;

    OrderStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
