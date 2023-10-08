package com.itsthatjun.ecommerce.dto.oms;

public enum ReturnStatusCode {
    WAITING_TO_PROCESS(0),
    RETURNING(1),
    COMPLETE(2),
    REJECTED(3);

    private final int code;

    ReturnStatusCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
