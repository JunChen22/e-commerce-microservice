package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.ConfirmOrderResult;
import com.itsthatjun.ecommerce.dto.OrderParam;
import com.itsthatjun.ecommerce.mbg.model.Address;
import com.itsthatjun.ecommerce.mbg.model.Orders;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface OrderService {

    @ApiModelProperty("Get order detail")
    Mono<Orders> detail(String orderSN);

    @ApiModelProperty("list all user orders")
    Flux<Orders> list(int status, int pageNum, int pageSize, int userId);

    @Transactional
    @ApiModelProperty("Create the actual transaction and payment")
    Mono<ConfirmOrderResult> generateOrder(OrderParam orderParam, String successUrl, String cancelUrl, int userId);

    @Transactional
    @ApiModelProperty("payment successful, redirected from paypal")
    Mono<Orders> paySuccess(String orderSn, String paymentId, String payerId);

    @ApiModelProperty("payment unsuccessful, redirected from paypal")
    void payFail(String orderSN);

    // TODO: create update method
    @ApiModelProperty("update on status, or other infomation")
    Mono<Orders> update();

    @Transactional
    @ApiModelProperty("Member cancel order")
    String cancelOrder(String orderSN);
}
