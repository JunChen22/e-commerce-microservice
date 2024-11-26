package com.itsthatjun.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InvalidVerificationTokenException extends RuntimeException{
    public InvalidVerificationTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
