package com.itsthatjun.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MemberException extends RuntimeException {
    public MemberException(String message) {
        super(message);
    }
}
