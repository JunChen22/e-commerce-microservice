package com.itsthatjun.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class InvalidArticleStateException extends RuntimeException{
    public InvalidArticleStateException(String message) {
        super(message);
    }
}
