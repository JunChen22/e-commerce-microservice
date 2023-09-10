package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.ConfirmOrderResult;
import com.itsthatjun.ecommerce.dto.OrderParam;
import com.itsthatjun.ecommerce.mbg.model.Orders;
import io.swagger.annotations.ApiOperation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface OrderService {

    @ApiOperation("Get order detail")
    Mono<Orders> detail(String orderSN);

    @ApiOperation("list all user orders")
    Flux<Orders> list(int status, int pageNum, int pageSize, int userId);

    @Transactional
    @ApiOperation("Create the actual transaction and payment")
    Mono<ConfirmOrderResult> generateOrder(OrderParam orderParam, String successUrl, String cancelUrl, int userId);

    @Transactional
    @ApiOperation("payment successful, redirected from paypal")
    Mono<Orders> paySuccess(String orderSn, String paymentId, String payerId);

    @ApiOperation("payment unsuccessful, redirected from paypal")
    void payFail(String orderSN);

    // TODO: create update method
    @ApiOperation("update on status, or other information")
    Mono<Orders> update();

    @Transactional
    @ApiOperation("Member cancel order")
    String cancelOrder(String orderSN);
}
