package com.itsthatjun.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.LOCKED)
public class UserLockedOutException extends RuntimeException{
    public UserLockedOutException(String message) {
        super(message);
    }
}