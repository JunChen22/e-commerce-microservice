package com.itsthatjun.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class CustomResponseEntityExceptionHandler {

    /*
    TODO: Implement exception handling
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ResponseEntity<String>> handleArticleIdException(ArticleException ex) {
        return Mono.just(new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ResponseEntity<String>> handleOrderException(OrderException ex) {
        return Mono.just(new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ResponseEntity<String>> handleOrderReturnApplyException(OrderReturnApplyException ex) {
        return Mono.just(new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ResponseEntity<String>> handleProductException(ProductException ex) {
        return Mono.just(new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ResponseEntity<String>> handleMemberException(MembersException ex) {
        return Mono.just(new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

     */
}
