package com.itsthatjun.ecommerce.service.OMS;

import com.itsthatjun.ecommerce.dto.oms.OrderParam;
import com.itsthatjun.ecommerce.dto.oms.OrderDetail;
import com.itsthatjun.ecommerce.dto.oms.model.OrderDTO;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderService {

    @ApiOperation("Get Order Detail by serial number")
    Mono<OrderDetail> detail(String orderSn);

    @ApiOperation("Get member order based on status code and page size")
    Flux<OrderDTO> list(int status, Integer pageNum, Integer pageSize);

    @ApiOperation(value = "get payment link to make payment in different time")
    Mono<String> getPaymentLink(String orderSn);

    @ApiOperation(value = "Generate order based on shopping cart, actual transaction")
    Mono<OrderParam> generateOrder(OrderParam orderParam, String requestUrl);

    @ApiOperation("after success paypal payment, actual processing the order , unlock stocks and update info's like coupon and stocks")
    Mono<String> successPay(String paymentId, String payerId);

    @ApiOperation("Cancel order if before sending the order out.")
    Mono<Void> cancelUserOrder(String orderSn);
}
