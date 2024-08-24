package com.itsthatjun.ecommerce.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class CustomResponseEntityExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleOrderException(CouponException ex) {
        return new ResponseEntity(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleOrderReturnApplyException(SaleException ex) {
        return new ResponseEntity(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}