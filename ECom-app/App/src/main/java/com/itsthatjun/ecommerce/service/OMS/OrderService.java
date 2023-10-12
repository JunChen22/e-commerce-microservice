package com.itsthatjun.ecommerce.service.OMS;

import com.itsthatjun.ecommerce.dto.OrderParam;
import com.itsthatjun.ecommerce.mbg.model.Orders;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderService {

    @ApiOperation("Get Order Detail by serial number")
    Mono<Orders> detail(String orderSn, int userId);

    @ApiOperation("Get member order based on status code and page size")
    Flux<Orders> list(int status, Integer pageNum, Integer pageSize, int userId);

    @ApiOperation(value = "Generate order based on shopping cart, actual transaction")
    Mono<OrderParam> generateOrder(OrderParam orderParam, String requestUrl, int userId);

    @ApiOperation("after success paypal payment, actual processing the order , unlock stocks and update info's like coupon and stocks")
    Mono<String> successPay(String paymentId, String payerId);

    @ApiOperation("Payment failure feedback")
    Mono<String> payFail(String orderSn,String token);

    @ApiOperation("Cancel order if before sending the order out.")
    Mono<Void> cancelUserOrder(String orderSn, int userId);
}
